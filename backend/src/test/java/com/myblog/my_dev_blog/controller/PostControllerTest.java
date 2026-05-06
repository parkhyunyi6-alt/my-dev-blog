package com.myblog.my_dev_blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblog.my_dev_blog.config.CustomOAuth2UserService;
import com.myblog.my_dev_blog.config.JwtAuthenticationFilter;
import com.myblog.my_dev_blog.config.OAuth2SuccessHandler;
import com.myblog.my_dev_blog.config.SecurityConfig;
import com.myblog.my_dev_blog.dto.request.LikeRequest;
import com.myblog.my_dev_blog.dto.request.PostCreateRequest;
import com.myblog.my_dev_blog.dto.request.PostUpdateRequest;
import com.myblog.my_dev_blog.dto.response.*;
import com.myblog.my_dev_blog.exception.NotFoundException;
import com.myblog.my_dev_blog.service.PostService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client",
    "spring.security.oauth2.client.registration.google.client-secret=test-secret"
})
class PostControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean PostService postService;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean CustomOAuth2UserService customOAuth2UserService;
    @MockBean OAuth2SuccessHandler oAuth2SuccessHandler;

    @BeforeEach
    void setUp() throws Exception {
        // mock이 filter chain을 통과하도록 설정
        doAnswer(inv -> {
            FilterChain chain = inv.getArgument(2);
            chain.doFilter(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter)
          .doFilter(any(ServletRequest.class), any(ServletResponse.class), any(FilterChain.class));
    }

    private Authentication ownerAuth() {
        return new UsernamePasswordAuthenticationToken(
                1L, null, List.of(new SimpleGrantedAuthority("ROLE_OWNER")));
    }

    private Authentication guestAuth() {
        return new UsernamePasswordAuthenticationToken(
                2L, null, List.of(new SimpleGrantedAuthority("ROLE_GUEST")));
    }

    private PostDetailResponse dummyDetail() {
        return new PostDetailResponse(1L, "제목", "내용", null, List.of(), null, 0, 0, null, null);
    }

    // ─── GET /api/posts ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/posts")
    class GetPosts {

        @Test
        @DisplayName("인증 없이 200 OK를 반환한다")
        void 포스트_목록_공개() throws Exception {
            given(postService.getPosts(null, 1, 10))
                    .willReturn(new PagedPostResponse(List.of(), 0, 0L, 1));

            mockMvc.perform(get("/api/posts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("categoryId 파라미터를 서비스에 전달한다")
        void 카테고리_필터_전달() throws Exception {
            given(postService.getPosts(5L, 1, 10))
                    .willReturn(new PagedPostResponse(List.of(), 0, 0L, 1));

            mockMvc.perform(get("/api/posts").param("categoryId", "5"))
                    .andExpect(status().isOk());
        }
    }

    // ─── GET /api/posts/latest ───────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/posts/latest")
    class GetLatestPost {

        @Test
        @DisplayName("인증 없이 최신 포스트를 반환한다")
        void 최신_포스트_조회() throws Exception {
            given(postService.getLatestPost()).willReturn(dummyDetail());

            mockMvc.perform(get("/api/posts/latest"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("포스트가 없으면 404")
        void 포스트_없으면_404() throws Exception {
            given(postService.getLatestPost())
                    .willThrow(new NotFoundException("포스트가 없습니다."));

            mockMvc.perform(get("/api/posts/latest"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ─── GET /api/posts/{id} ─────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/posts/{id}")
    class GetPost {

        @Test
        @DisplayName("인증 없이 포스트 상세를 반환한다")
        void 포스트_상세_공개() throws Exception {
            given(postService.getPost(1L)).willReturn(dummyDetail());

            mockMvc.perform(get("/api/posts/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.title").value("제목"));
        }

        @Test
        @DisplayName("존재하지 않는 포스트는 404")
        void 포스트_없으면_404() throws Exception {
            given(postService.getPost(999L))
                    .willThrow(new NotFoundException("포스트를 찾을 수 없습니다."));

            mockMvc.perform(get("/api/posts/999"))
                    .andExpect(status().isNotFound());
        }
    }

    // ─── POST /api/posts ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/posts")
    class CreatePost {

        private String validJson() throws Exception {
            return objectMapper.writeValueAsString(
                    new PostCreateRequest("제목", "내용", 1L, List.of("tag1")));
        }

        @Test
        @DisplayName("OWNER는 201 Created로 포스트를 생성한다")
        void OWNER_생성_성공() throws Exception {
            given(postService.createPost(any(), eq(1L))).willReturn(dummyDetail());

            mockMvc.perform(post("/api/posts")
                            .with(authentication(ownerAuth()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validJson()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("인증 없는 요청은 401 Unauthorized")
        void 미인증_401() throws Exception {
            mockMvc.perform(post("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validJson()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GUEST는 403 Forbidden")
        void GUEST_403() throws Exception {
            mockMvc.perform(post("/api/posts")
                            .with(authentication(guestAuth()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validJson()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("필수 필드가 없으면 400 Bad Request")
        void 필수_필드_없으면_400() throws Exception {
            String invalidJson = objectMapper.writeValueAsString(
                    new PostCreateRequest("", "내용", 1L, List.of()));

            mockMvc.perform(post("/api/posts")
                            .with(authentication(ownerAuth()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── PUT /api/posts/{id} ─────────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /api/posts/{id}")
    class UpdatePost {

        private String validJson() throws Exception {
            return objectMapper.writeValueAsString(
                    new PostUpdateRequest("새제목", "새내용", 1L, List.of()));
        }

        @Test
        @DisplayName("OWNER는 포스트를 수정한다")
        void OWNER_수정_성공() throws Exception {
            given(postService.updatePost(eq(1L), any(), eq(1L))).willReturn(dummyDetail());

            mockMvc.perform(put("/api/posts/1")
                            .with(authentication(ownerAuth()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validJson()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("GUEST는 403 Forbidden")
        void GUEST_403() throws Exception {
            mockMvc.perform(put("/api/posts/1")
                            .with(authentication(guestAuth()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validJson()))
                    .andExpect(status().isForbidden());
        }
    }

    // ─── DELETE /api/posts/{id} ──────────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/posts/{id}")
    class DeletePost {

        @Test
        @DisplayName("OWNER는 포스트를 삭제한다")
        void OWNER_삭제_성공() throws Exception {
            mockMvc.perform(delete("/api/posts/1")
                            .with(authentication(ownerAuth())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("GUEST는 403 Forbidden")
        void GUEST_403() throws Exception {
            mockMvc.perform(delete("/api/posts/1")
                            .with(authentication(guestAuth())))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("인증 없는 요청은 401 Unauthorized")
        void 미인증_401() throws Exception {
            mockMvc.perform(delete("/api/posts/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─── POST /api/posts/{id}/views ──────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/posts/{id}/views")
    class IncrementViewCount {

        @Test
        @DisplayName("인증 없이 조회수를 증가시킨다")
        void 조회수_증가_공개() throws Exception {
            mockMvc.perform(post("/api/posts/1/views"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("포스트가 없으면 GlobalExceptionHandler가 404를 반환한다")
        void 포스트_없으면_404() throws Exception {
            doThrow(new NotFoundException("포스트를 찾을 수 없습니다."))
                    .when(postService).incrementViewCount(999L);

            mockMvc.perform(post("/api/posts/999/views"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("포스트를 찾을 수 없습니다."));
        }
    }

    // ─── POST /api/posts/{id}/likes ──────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/posts/{id}/likes")
    class AddLike {

        @Test
        @DisplayName("비로그인 유저가 deviceId로 좋아요를 추가한다")
        void 비로그인_좋아요() throws Exception {
            String json = objectMapper.writeValueAsString(new LikeRequest("device-abc"));

            mockMvc.perform(post("/api/posts/1/likes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("로그인 유저도 좋아요를 추가할 수 있다")
        void 로그인_좋아요() throws Exception {
            String json = objectMapper.writeValueAsString(new LikeRequest("device-abc"));

            mockMvc.perform(post("/api/posts/1/likes")
                            .with(authentication(guestAuth()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk());
        }
    }

    // ─── DELETE /api/posts/{id}/likes ────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/posts/{id}/likes")
    class RemoveLike {

        @Test
        @DisplayName("비로그인 유저가 deviceId로 좋아요를 취소한다")
        void 비로그인_좋아요_취소() throws Exception {
            String json = objectMapper.writeValueAsString(new LikeRequest("device-abc"));

            mockMvc.perform(delete("/api/posts/1/likes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    // ─── GET /api/posts/{id}/likes/status ────────────────────────────────────

    @Nested
    @DisplayName("GET /api/posts/{id}/likes/status")
    class IsLiked {

        @Test
        @DisplayName("비로그인 — deviceId로 좋아요 여부를 반환한다")
        void 비로그인_좋아요_상태() throws Exception {
            given(postService.isLiked(eq(1L), isNull(), eq("device-abc"))).willReturn(true);

            mockMvc.perform(get("/api/posts/1/likes/status")
                            .param("deviceId", "device-abc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.liked").value(true));
        }

        @Test
        @DisplayName("로그인 유저는 userId로 좋아요 여부를 반환한다")
        void 로그인_좋아요_상태() throws Exception {
            given(postService.isLiked(eq(1L), eq(2L), isNull())).willReturn(false);

            mockMvc.perform(get("/api/posts/1/likes/status")
                            .with(authentication(guestAuth())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.liked").value(false));
        }
    }
}
