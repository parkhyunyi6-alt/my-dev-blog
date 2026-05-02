package com.myblog.my_dev_blog.repository;

import com.myblog.my_dev_blog.entity.Post;
import com.myblog.my_dev_blog.entity.PostLike;
import com.myblog.my_dev_blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);

    Optional<PostLike> findByPostAndDeviceId(Post post, String deviceId);

    boolean existsByPostAndUser(Post post, User user);

    boolean existsByPostAndDeviceId(Post post, String deviceId);
}
