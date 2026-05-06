package com.myblog.my_dev_blog.repository;

import com.myblog.my_dev_blog.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PostTagRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired PostTagRepository postTagRepository;

    Post post;

    @BeforeEach
    void setUp() {
        CategoryGroup group = em.persist(new CategoryGroup("그룹", 0));
        Category category = em.persist(new Category(group, "카테고리", 0));
        User user = em.persist(new User("g1", "u@test.com", "유저", null, User.Role.GUEST));
        post = em.persist(new Post(user, category, "제목", "내용"));

        Tag tag1 = em.persist(new Tag("spring"));
        Tag tag2 = em.persist(new Tag("jpa"));
        em.persist(new PostTag(post, tag1));
        em.persist(new PostTag(post, tag2));

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("findByPost — 포스트에 연결된 태그를 모두 반환한다")
    void findByPost_태그_목록_반환() {
        Post savedPost = em.find(Post.class, post.getId());

        List<PostTag> result = postTagRepository.findByPost(savedPost);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(pt -> pt.getTag().getName())
                .containsExactlyInAnyOrder("spring", "jpa");
    }

    @Test
    @DisplayName("deleteByPost — @Modifying JPQL로 즉시 삭제되어 이후 조회 시 빈 결과를 반환한다")
    void deleteByPost_즉시_삭제() {
        Post savedPost = em.find(Post.class, post.getId());
        assertThat(postTagRepository.findByPost(savedPost)).hasSize(2);

        postTagRepository.deleteByPost(savedPost);
        // @Modifying JPQL은 1차 캐시를 우회하므로 clear 없이도 DB 반영 확인 가능.
        // 단, 이후 findByPost는 DB를 조회하므로 삭제 결과가 즉시 반영된다.
        em.clear();

        List<PostTag> result = postTagRepository.findByPost(savedPost);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deleteByPost — 삭제 후 새 태그를 저장해도 유니크 제약 위반이 발생하지 않는다")
    void deleteByPost_후_재저장_성공() {
        Post savedPost = em.find(Post.class, post.getId());
        Tag springTag = em.find(Tag.class,
                postTagRepository.findByPost(savedPost).get(0).getTag().getId());

        postTagRepository.deleteByPost(savedPost);
        em.flush();

        PostTag reCreated = postTagRepository.save(new PostTag(savedPost, springTag));

        assertThat(reCreated.getId()).isNotNull();
    }
}
