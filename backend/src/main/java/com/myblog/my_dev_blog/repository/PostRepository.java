package com.myblog.my_dev_blog.repository;

import com.myblog.my_dev_blog.entity.Category;
import com.myblog.my_dev_blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    Page<Post> findByCategory(Category category, Pageable pageable);

    Optional<Post> findTopByOrderByCreatedAtDesc();

    boolean existsByCategory(Category category);

    // 현재 포스트 기준 이후 N개 조회
    @Query("SELECT p FROM Post p WHERE p.id > :postId ORDER BY p.id ASC")
    List<Post> findNextPosts(@Param("postId") Long postId, Pageable pageable);

    // 현재 포스트 기준 이전 N개 조회
    @Query("SELECT p FROM Post p WHERE p.id < :postId ORDER BY p.id DESC")
    List<Post> findPrevPosts(@Param("postId") Long postId, Pageable pageable);

    // 태그별 포스트 목록 조회
    @Query("SELECT p FROM Post p WHERE p.id IN (SELECT pt.post.id FROM PostTag pt WHERE pt.tag.id = :tagId)")
    Page<Post> findByTagId(@Param("tagId") Long tagId, Pageable pageable);
}
