package com.myblog.my_dev_blog.repository;

import com.myblog.my_dev_blog.entity.Post;
import com.myblog.my_dev_blog.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    List<PostTag> findByPost(Post post);

    void deleteByPost(Post post);
}
