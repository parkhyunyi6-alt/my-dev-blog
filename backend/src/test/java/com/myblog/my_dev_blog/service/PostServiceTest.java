package com.myblog.my_dev_blog.service;

import com.myblog.my_dev_blog.dto.request.PostCreateRequest;
import com.myblog.my_dev_blog.dto.request.PostUpdateRequest;
import com.myblog.my_dev_blog.entity.*;
import com.myblog.my_dev_blog.exception.ConflictException;
import com.myblog.my_dev_blog.exception.ForbiddenException;
import com.myblog.my_dev_blog.exception.NotFoundException;
import com.myblog.my_dev_blog.repository.*;
import com.myblog.my_dev_blog.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock PostRepository postRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock UserRepository userRepository;
    @Mock TagRepository tagRepository;
    @Mock PostTagRepository postTagRepository;
    @Mock PostLikeRepository postLikeRepository;

    @InjectMocks PostServiceImpl postService;

    User owner;
    User guest;
    Category category;
    Post post;

    @BeforeEach
    void setUp() {
        CategoryGroup group = new CategoryGroup("그룹", 0);
        ReflectionTestUtils.setField(group, "id", 1L);

        owner = new User("g-owner", "owner@test.com", "오너", null, User.Role.OWNER);
        ReflectionTestUtils.setField(owner, "id", 1L);

        guest = new User("g-guest", "guest@test.com", "게스트", null, User.Role.GUEST);
        ReflectionTestUtils.setField(guest, "id", 2L);

        category = new Category(group, "카테고리", 0);
        ReflectionTestUtils.setField(category, "id", 10L);

        post = new Post(owner, category, "제목", "내용");
        ReflectionTestUtils.setField(post, "id", 100L);
    }

    // ─── createPost ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("createPost")
    class CreatePost {

        @Test
        @DisplayName("OWNER는 포스트를 생성할 수 있다")
        void OWNER_생성_성공() {
            PostCreateRequest req = new PostCreateRequest("제목", "내용", 10L, List.of("tag1"));
            Tag tag = new Tag("tag1");
            ReflectionTestUtils.setField(tag, "id", 1L);

            given(userRepository.findById(1L)).willReturn(Optional.of(owner));
            given(categoryRepository.findById(10L)).willReturn(Optional.of(category));
            given(postRepository.save(any(Post.class))).willReturn(post);
            given(tagRepository.findByName("tag1")).willReturn(Optional.of(tag));
            given(postTagRepository.save(any(PostTag.class))).willReturn(new PostTag(post, tag));

            var result = postService.createPost(req, 1L);

            assertThat(result.title()).isEqualTo("제목");
            assertThat(result.tags()).hasSize(1);
            verify(postRepository).save(any(Post.class));
        }

        @Test
        @DisplayName("GUEST가 생성하면 ForbiddenException")
        void GUEST_생성_거부() {
            PostCreateRequest req = new PostCreateRequest("제목", "내용", 10L, List.of());
            given(userRepository.findById(2L)).willReturn(Optional.of(guest));

            assertThatThrownBy(() -> postService.createPost(req, 2L))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessageContaining("권한");

            verify(postRepository, never()).save(any());
        }

        @Test
        @DisplayName("카테고리가 없으면 NotFoundException")
        void 카테고리_없으면_예외() {
            PostCreateRequest req = new PostCreateRequest("제목", "내용", 99L, List.of());
            given(userRepository.findById(1L)).willReturn(Optional.of(owner));
            given(categoryRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.createPost(req, 1L))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("태그가 없으면 postTagRepository를 호출하지 않는다")
        void 태그_없으면_postTag_저장_안함() {
            PostCreateRequest req = new PostCreateRequest("제목", "내용", 10L, null);
            given(userRepository.findById(1L)).willReturn(Optional.of(owner));
            given(categoryRepository.findById(10L)).willReturn(Optional.of(category));
            given(postRepository.save(any(Post.class))).willReturn(post);

            postService.createPost(req, 1L);

            verify(postTagRepository, never()).save(any());
        }
    }

    // ─── updatePost ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("updatePost")
    class UpdatePost {

        @Test
        @DisplayName("OWNER는 포스트를 수정할 수 있다")
        void OWNER_수정_성공() {
            PostUpdateRequest req = new PostUpdateRequest("새제목", "새내용", 10L, List.of());
            given(userRepository.findById(1L)).willReturn(Optional.of(owner));
            given(postRepository.findById(100L)).willReturn(Optional.of(post));
            given(categoryRepository.findById(10L)).willReturn(Optional.of(category));

            var result = postService.updatePost(100L, req, 1L);

            assertThat(result.title()).isEqualTo("새제목");
            verify(postTagRepository).deleteByPost(post);
        }

        @Test
        @DisplayName("GUEST가 수정하면 ForbiddenException")
        void GUEST_수정_거부() {
            PostUpdateRequest req = new PostUpdateRequest("새제목", "새내용", 10L, List.of());
            given(userRepository.findById(2L)).willReturn(Optional.of(guest));

            assertThatThrownBy(() -> postService.updatePost(100L, req, 2L))
                    .isInstanceOf(ForbiddenException.class);
        }

        @Test
        @DisplayName("포스트가 없으면 NotFoundException")
        void 포스트_없으면_예외() {
            PostUpdateRequest req = new PostUpdateRequest("새제목", "새내용", 10L, List.of());
            given(userRepository.findById(1L)).willReturn(Optional.of(owner));
            given(postRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.updatePost(999L, req, 1L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ─── deletePost ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deletePost")
    class DeletePost {

        @Test
        @DisplayName("OWNER는 포스트와 연관 데이터를 모두 삭제한다")
        void OWNER_삭제_성공() {
            given(userRepository.findById(1L)).willReturn(Optional.of(owner));
            given(postRepository.findById(100L)).willReturn(Optional.of(post));

            postService.deletePost(100L, 1L);

            verify(postTagRepository).deleteByPost(post);
            verify(postLikeRepository).deleteByPost(post);
            verify(postRepository).delete(post);
        }

        @Test
        @DisplayName("GUEST가 삭제하면 ForbiddenException")
        void GUEST_삭제_거부() {
            given(userRepository.findById(2L)).willReturn(Optional.of(guest));

            assertThatThrownBy(() -> postService.deletePost(100L, 2L))
                    .isInstanceOf(ForbiddenException.class);

            verify(postRepository, never()).delete(any());
        }

        @Test
        @DisplayName("포스트가 없으면 NotFoundException")
        void 포스트_없으면_예외() {
            given(userRepository.findById(1L)).willReturn(Optional.of(owner));
            given(postRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.deletePost(999L, 1L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ─── addLike ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("addLike")
    class AddLike {

        @Test
        @DisplayName("로그인 유저가 처음 좋아요를 누르면 incrementHeartCount를 호출한다")
        void 로그인_좋아요_성공() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));
            given(userRepository.findById(2L)).willReturn(Optional.of(guest));
            given(postLikeRepository.existsByPostAndUser(post, guest)).willReturn(false);
            given(postLikeRepository.existsByPostAndDeviceId(post, "device-1")).willReturn(false);
            given(postLikeRepository.save(any())).willReturn(new PostLike(post, guest, "device-1"));

            postService.addLike(100L, 2L, "device-1");

            verify(postLikeRepository).save(any());
            verify(postRepository).incrementHeartCount(100L);
        }

        @Test
        @DisplayName("로그인 유저가 이미 좋아요한 포스트면 ConflictException")
        void 로그인_중복_좋아요_ConflictException() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));
            given(userRepository.findById(2L)).willReturn(Optional.of(guest));
            given(postLikeRepository.existsByPostAndUser(post, guest)).willReturn(true);

            assertThatThrownBy(() -> postService.addLike(100L, 2L, "device-1"))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("이미 좋아요");

            verify(postLikeRepository, never()).save(any());
        }

        @Test
        @DisplayName("로그인 유저가 비로그인으로 누른 기기 deviceId로 요청하면 ConflictException")
        void 로그인_deviceId_중복_ConflictException() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));
            given(userRepository.findById(2L)).willReturn(Optional.of(guest));
            given(postLikeRepository.existsByPostAndUser(post, guest)).willReturn(false);
            given(postLikeRepository.existsByPostAndDeviceId(post, "device-1")).willReturn(true);

            assertThatThrownBy(() -> postService.addLike(100L, 2L, "device-1"))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("이미 좋아요");

            verify(postLikeRepository, never()).save(any());
        }

        @Test
        @DisplayName("비로그인 유저가 처음 좋아요를 누르면 incrementHeartCount를 호출한다")
        void 비로그인_좋아요_성공() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));
            given(postLikeRepository.existsByPostAndDeviceId(post, "device-1")).willReturn(false);
            given(postLikeRepository.save(any())).willReturn(new PostLike(post, "device-1"));

            postService.addLike(100L, null, "device-1");

            verify(postRepository).incrementHeartCount(100L);
        }

        @Test
        @DisplayName("비로그인 유저가 같은 deviceId로 중복 좋아요하면 ConflictException")
        void 비로그인_중복_좋아요_ConflictException() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));
            given(postLikeRepository.existsByPostAndDeviceId(post, "device-1")).willReturn(true);

            assertThatThrownBy(() -> postService.addLike(100L, null, "device-1"))
                    .isInstanceOf(ConflictException.class);
        }

        @Test
        @DisplayName("비로그인 유저가 deviceId 없이 요청하면 IllegalArgumentException")
        void 비로그인_deviceId_없음_예외() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));

            assertThatThrownBy(() -> postService.addLike(100L, null, ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("deviceId");
        }
    }

    // ─── removeLike ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("removeLike")
    class RemoveLike {

        @Test
        @DisplayName("로그인 유저가 좋아요를 취소하면 like row 삭제 후 decrementHeartCount를 호출한다")
        void 로그인_좋아요_취소_성공() {
            PostLike like = new PostLike(post, guest, "device-1");
            given(postRepository.findById(100L)).willReturn(Optional.of(post));
            given(userRepository.findById(2L)).willReturn(Optional.of(guest));
            given(postLikeRepository.findByPostAndUser(post, guest)).willReturn(Optional.of(like));

            postService.removeLike(100L, 2L, "device-1");

            verify(postLikeRepository).delete(like);
            verify(postRepository).decrementHeartCount(100L);
        }

        @Test
        @DisplayName("로그인 유저의 좋아요가 없으면 NotFoundException")
        void 로그인_좋아요_없으면_예외() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));
            given(userRepository.findById(2L)).willReturn(Optional.of(guest));
            given(postLikeRepository.findByPostAndUser(post, guest)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.removeLike(100L, 2L, "device-1"))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("좋아요");
        }

        @Test
        @DisplayName("비로그인 유저가 deviceId로 좋아요를 취소한다")
        void 비로그인_좋아요_취소_성공() {
            PostLike like = new PostLike(post, "device-1");
            given(postRepository.findById(100L)).willReturn(Optional.of(post));
            given(postLikeRepository.findByPostAndDeviceId(post, "device-1")).willReturn(Optional.of(like));

            postService.removeLike(100L, null, "device-1");

            verify(postLikeRepository).delete(like);
            verify(postRepository).decrementHeartCount(100L);
        }

        @Test
        @DisplayName("비로그인 유저가 deviceId 없이 요청하면 IllegalArgumentException")
        void 비로그인_deviceId_없음_예외() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));

            assertThatThrownBy(() -> postService.removeLike(100L, null, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ─── incrementViewCount ─────────────────────────────────────────────────

    @Nested
    @DisplayName("incrementViewCount")
    class IncrementViewCount {

        @Test
        @DisplayName("포스트가 존재하면 incrementViewCount 쿼리를 호출한다")
        void 조회수_1_증가() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));

            postService.incrementViewCount(100L);

            verify(postRepository).incrementViewCount(100L);
        }

        @Test
        @DisplayName("포스트가 없으면 NotFoundException")
        void 포스트_없으면_예외() {
            given(postRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.incrementViewCount(999L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ─── isLiked ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("isLiked")
    class IsLiked {

        @Test
        @DisplayName("로그인 유저가 좋아요했으면 true를 반환한다")
        void 로그인_좋아요_true() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));
            given(userRepository.findById(2L)).willReturn(Optional.of(guest));
            given(postLikeRepository.existsByPostAndUser(post, guest)).willReturn(true);

            assertThat(postService.isLiked(100L, 2L, null)).isTrue();
        }

        @Test
        @DisplayName("로그인 유저가 좋아요하지 않았으면 false를 반환한다")
        void 로그인_좋아요_false() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));
            given(userRepository.findById(2L)).willReturn(Optional.of(guest));
            given(postLikeRepository.existsByPostAndUser(post, guest)).willReturn(false);

            assertThat(postService.isLiked(100L, 2L, null)).isFalse();
        }

        @Test
        @DisplayName("비로그인 유저는 deviceId 기반으로 좋아요 여부를 반환한다")
        void 비로그인_device_기반_조회() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));
            given(postLikeRepository.existsByPostAndDeviceId(post, "device-1")).willReturn(true);

            assertThat(postService.isLiked(100L, null, "device-1")).isTrue();
        }

        @Test
        @DisplayName("userId도 deviceId도 없으면 false를 반환한다")
        void 둘_다_없으면_false() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));

            assertThat(postService.isLiked(100L, null, null)).isFalse();
        }

        @Test
        @DisplayName("빈 deviceId는 false로 처리한다")
        void 빈_deviceId_false() {
            given(postRepository.findById(100L)).willReturn(Optional.of(post));

            assertThat(postService.isLiked(100L, null, "")).isFalse();
        }
    }
}
