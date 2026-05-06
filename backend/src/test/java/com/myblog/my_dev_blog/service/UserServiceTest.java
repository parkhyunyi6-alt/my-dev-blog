package com.myblog.my_dev_blog.service;

import com.myblog.my_dev_blog.entity.User;
import com.myblog.my_dev_blog.exception.NotFoundException;
import com.myblog.my_dev_blog.repository.UserRepository;
import com.myblog.my_dev_blog.service.impl.UserServiceImpl;
import com.myblog.my_dev_blog.util.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock JwtTokenProvider jwtTokenProvider;
    @InjectMocks UserServiceImpl userService;

    private User makeUser(Long id, User.Role role) {
        User user = new User("google-" + id, "user" + id + "@test.com", "이름" + id, "https://img/" + id, role);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    @Nested
    @DisplayName("findOrCreateUser")
    class FindOrCreateUser {

        @Test
        @DisplayName("googleId가 없으면 GUEST 역할로 신규 저장한다")
        void 신규_유저_생성() {
            User saved = makeUser(1L, User.Role.GUEST);
            given(userRepository.findByGoogleId("google-1")).willReturn(Optional.empty());
            given(userRepository.save(any(User.class))).willReturn(saved);

            User result = userService.findOrCreateUser("google-1", "user1@test.com", "이름1", null);

            assertThat(result.getRole()).isEqualTo(User.Role.GUEST);
            assertThat(result.getGoogleId()).isEqualTo("google-1");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("googleId가 있으면 이름·이미지를 업데이트하고 save를 호출하지 않는다")
        void 기존_유저_프로필_업데이트() {
            User existing = makeUser(1L, User.Role.GUEST);
            given(userRepository.findByGoogleId("google-1")).willReturn(Optional.of(existing));

            User result = userService.findOrCreateUser("google-1", "user1@test.com", "새이름", "새이미지");

            assertThat(result.getName()).isEqualTo("새이름");
            assertThat(result.getProfileImageUrl()).isEqualTo("새이미지");
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("기존 유저의 역할은 변경되지 않는다")
        void 기존_유저_역할_유지() {
            User existing = makeUser(1L, User.Role.OWNER);
            given(userRepository.findByGoogleId("google-1")).willReturn(Optional.of(existing));

            User result = userService.findOrCreateUser("google-1", "user1@test.com", "이름1", null);

            assertThat(result.getRole()).isEqualTo(User.Role.OWNER);
        }
    }

    @Nested
    @DisplayName("loginOrRegister")
    class LoginOrRegister {

        @Test
        @DisplayName("findOrCreateUser에 위임하고 JWT 토큰을 반환한다")
        void JWT_생성_위임() {
            User saved = makeUser(1L, User.Role.GUEST);
            given(userRepository.findByGoogleId("google-1")).willReturn(Optional.empty());
            given(userRepository.save(any(User.class))).willReturn(saved);
            given(jwtTokenProvider.generateToken(1L, User.Role.GUEST)).willReturn("mock-token");

            String token = userService.loginOrRegister("google-1", "user1@test.com", "이름1", null);

            assertThat(token).isEqualTo("mock-token");
            verify(jwtTokenProvider).generateToken(1L, User.Role.GUEST);
        }

        @Test
        @DisplayName("기존 유저로 로그인해도 JWT가 반환된다")
        void 기존_유저_JWT_반환() {
            User existing = makeUser(2L, User.Role.OWNER);
            given(userRepository.findByGoogleId("google-2")).willReturn(Optional.of(existing));
            given(jwtTokenProvider.generateToken(2L, User.Role.OWNER)).willReturn("owner-token");

            String token = userService.loginOrRegister("google-2", "user2@test.com", "이름2", null);

            assertThat(token).isEqualTo("owner-token");
        }
    }

    @Nested
    @DisplayName("getMe")
    class GetMe {

        @Test
        @DisplayName("userId로 사용자를 조회하고 UserResponse를 반환한다")
        void 사용자_조회_성공() {
            User user = makeUser(1L, User.Role.GUEST);
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            var response = userService.getMe(1L);

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.email()).isEqualTo("user1@test.com");
            assertThat(response.role()).isEqualTo("GUEST");
        }

        @Test
        @DisplayName("userId가 존재하지 않으면 NotFoundException")
        void 사용자_없으면_예외() {
            given(userRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getMe(99L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("사용자");
        }
    }
}
