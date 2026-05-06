package com.myblog.my_dev_blog.repository;

import com.myblog.my_dev_blog.entity.Post;
import com.myblog.my_dev_blog.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    List<PostTag> findByPost(Post post);

    // derived delete는 SELECT 후 remove() 호출 — flush 전까지 DB에 반영 안 될 수 있음.
    // @Modifying JPQL로 즉시 DELETE 실행하여 unique 제약 충돌 방지.
    @Modifying
    @Query("DELETE FROM PostTag pt WHERE pt.post = :post")
    void deleteByPost(@Param("post") Post post);
}
