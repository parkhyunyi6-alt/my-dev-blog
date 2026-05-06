package com.myblog.my_dev_blog.config;

import com.myblog.my_dev_blog.entity.User;
import com.myblog.my_dev_blog.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Long userId   = (Long)   oAuth2User.getAttributes().get("userId");
        String role   = (String) oAuth2User.getAttributes().get("userRole");

        String token = jwtTokenProvider.generateToken(userId, User.Role.valueOf(role));
        response.sendRedirect(frontendUrl + "?token=" + token);
    }
}
