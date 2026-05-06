package com.myblog.my_dev_blog.repository;

import com.myblog.my_dev_blog.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PostRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired PostRepository postRepository;

    Post post;

    @BeforeEach
    void setUp() {
        CategoryGroup group = em.persist(new CategoryGroup("그룹", 0));
        Category category = em.persist(new Category(group, "카테고리", 0));
        User user = em.persist(new User("g1", "u@test.com", "유저", null, User.Role.GUEST));
        post = em.persist(new Post(user, category, "제목", "내용"));
        em.flush();
        em.clear();
    }

    @Nested
    @DisplayName("incrementViewCount")
    class IncrementViewCount {

        @Test
        @DisplayName("viewCount가 DB에서 원자적으로 1 증가한다")
        void 조회수_원자_증가() {
            postRepository.incrementViewCount(post.getId());
            em.flush();
            em.clear();

            Post fresh = em.find(Post.class, post.getId());
            assertThat(fresh.getViewCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("두 번 호출하면 viewCount가 2가 된다")
        void 조회수_두번_증가() {
            postRepository.incrementViewCount(post.getId());
            postRepository.incrementViewCount(post.getId());
            em.flush();
            em.clear();

            Post fresh = em.find(Post.class, post.getId());
            assertThat(fresh.getViewCount()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("incrementHeartCount")
    class IncrementHeartCount {

        @Test
        @DisplayName("heartCount가 DB에서 원자적으로 1 증가한다")
        void 좋아요_원자_증가() {
            postRepository.incrementHeartCount(post.getId());
            em.flush();
            em.clear();

            Post fresh = em.find(Post.class, post.getId());
            assertThat(fresh.getHeartCount()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("decrementHeartCount")
    class DecrementHeartCount {

        @Test
        @DisplayName("heartCount가 양수이면 1 감소한다")
        void 좋아요_원자_감소() {
            postRepository.incrementHeartCount(post.getId());
            em.flush();
            em.clear();

            postRepository.decrementHeartCount(post.getId());
            em.flush();
            em.clear();

            Post fresh = em.find(Post.class, post.getId());
            assertThat(fresh.getHeartCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("heartCount가 0이면 감소하지 않는다 (underflow 방지)")
        void 좋아요_0일때_감소_안함() {
            postRepository.decrementHeartCount(post.getId());
            em.flush();
            em.clear();

            Post fresh = em.find(Post.class, post.getId());
            assertThat(fresh.getHeartCount()).isEqualTo(0);
        }
    }
}
