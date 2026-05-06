package com.myblog.my_dev_blog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // OAuth2 기본 경로
                .requestMatchers("/oauth2/**", "/login/**").permitAll()

                // 공개 GET
                .requestMatchers(HttpMethod.GET, "/api/category-groups").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()

                // 조회수 증가 (공개 POST — 구체적인 경로를 OWNER POST보다 먼저 선언)
                .requestMatchers(HttpMethod.POST, "/api/posts/*/views").permitAll()

                // 좋아요 추가/취소 (선택적 인증 — OWNER 규칙보다 먼저 선언)
                .requestMatchers(HttpMethod.POST, "/api/posts/*/likes").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/posts/*/likes").permitAll()

                // 인증 필요
                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()

                // OWNER 전용
                .requestMatchers(HttpMethod.POST, "/api/category-groups").hasRole("OWNER")
                .requestMatchers(HttpMethod.PUT, "/api/category-groups/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.DELETE, "/api/category-groups/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.POST, "/api/categories").hasRole("OWNER")
                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.POST, "/api/posts").hasRole("OWNER")
                .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasRole("OWNER")

                .anyRequest().denyAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, e) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                            "{\"success\":false,\"data\":null,\"message\":\"인증이 필요합니다.\"}");
                })
                .accessDeniedHandler((request, response, e) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                            "{\"success\":false,\"data\":null,\"message\":\"접근 권한이 없습니다.\"}");
                })
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo ->
                        userInfo.userService(customOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
