package com.myblog.my_dev_blog.repository;

import com.myblog.my_dev_blog.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PostLikeRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired PostLikeRepository postLikeRepository;

    Post post;
    User user;

    @BeforeEach
    void setUp() {
        CategoryGroup group = em.persist(new CategoryGroup("그룹", 0));
        Category category = em.persist(new Category(group, "카테고리", 0));
        user = em.persist(new User("g1", "u@test.com", "유저", null, User.Role.GUEST));
        post = em.persist(new Post(user, category, "제목", "내용"));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("findByPostAndUser — 로그인 유저의 좋아요를 조회한다")
    void findByPostAndUser_성공() {
        Post savedPost = em.find(Post.class, post.getId());
        User savedUser = em.find(User.class, user.getId());
        em.persist(new PostLike(savedPost, savedUser, "device-1"));
        em.flush();
        em.clear();

        Post freshPost = em.find(Post.class, post.getId());
        User freshUser = em.find(User.class, user.getId());

        Optional<PostLike> result = postLikeRepository.findByPostAndUser(freshPost, freshUser);

        assertThat(result).isPresent();
        assertThat(result.get().getDeviceId()).isEqualTo("device-1");
    }

    @Test
    @DisplayName("findByPostAndDeviceId — 비로그인 유저의 좋아요를 deviceId로 조회한다")
    void findByPostAndDeviceId_성공() {
        Post savedPost = em.find(Post.class, post.getId());
        em.persist(new PostLike(savedPost, "device-abc"));
        em.flush();
        em.clear();

        Post freshPost = em.find(Post.class, post.getId());

        Optional<PostLike> result = postLikeRepository.findByPostAndDeviceId(freshPost, "device-abc");

        assertThat(result).isPresent();
        assertThat(result.get().getUser()).isNull();
    }

    @Test
    @DisplayName("existsByPostAndUser — 로그인 유저 좋아요 존재 시 true")
    void existsByPostAndUser_존재() {
        Post savedPost = em.find(Post.class, post.getId());
        User savedUser = em.find(User.class, user.getId());
        em.persist(new PostLike(savedPost, savedUser));
        em.flush();
        em.clear();

        Post freshPost = em.find(Post.class, post.getId());
        User freshUser = em.find(User.class, user.getId());

        assertThat(postLikeRepository.existsByPostAndUser(freshPost, freshUser)).isTrue();
    }

    @Test
    @DisplayName("existsByPostAndUser — 좋아요 없으면 false")
    void existsByPostAndUser_없음() {
        Post freshPost = em.find(Post.class, post.getId());
        User freshUser = em.find(User.class, user.getId());

        assertThat(postLikeRepository.existsByPostAndUser(freshPost, freshUser)).isFalse();
    }

    @Test
    @DisplayName("existsByPostAndDeviceId — deviceId 좋아요 존재 시 true")
    void existsByPostAndDeviceId_존재() {
        Post savedPost = em.find(Post.class, post.getId());
        em.persist(new PostLike(savedPost, "device-xyz"));
        em.flush();
        em.clear();

        Post freshPost = em.find(Post.class, post.getId());

        assertThat(postLikeRepository.existsByPostAndDeviceId(freshPost, "device-xyz")).isTrue();
    }

    @Test
    @DisplayName("existsByPostAndDeviceId — 다른 deviceId면 false")
    void existsByPostAndDeviceId_다른_기기() {
        Post savedPost = em.find(Post.class, post.getId());
        em.persist(new PostLike(savedPost, "device-xyz"));
        em.flush();
        em.clear();

        Post freshPost = em.find(Post.class, post.getId());

        assertThat(postLikeRepository.existsByPostAndDeviceId(freshPost, "other-device")).isFalse();
    }

    @Test
    @DisplayName("deleteByPost — 포스트에 연결된 모든 좋아요를 삭제한다")
    void deleteByPost_전체_삭제() {
        Post savedPost = em.find(Post.class, post.getId());
        User savedUser = em.find(User.class, user.getId());
        em.persist(new PostLike(savedPost, savedUser));
        em.persist(new PostLike(savedPost, "device-1"));
        em.flush();
        em.clear();

        Post freshPost = em.find(Post.class, post.getId());
        postLikeRepository.deleteByPost(freshPost);
        em.flush();
        em.clear();

        Post verifyPost = em.find(Post.class, post.getId());
        User verifyUser = em.find(User.class, user.getId());

        assertThat(postLikeRepository.existsByPostAndUser(verifyPost, verifyUser)).isFalse();
        assertThat(postLikeRepository.existsByPostAndDeviceId(verifyPost, "device-1")).isFalse();
    }
}
