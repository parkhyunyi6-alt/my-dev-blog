package com.myblog.my_dev_blog.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "post_likes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "user_id"}),
        @UniqueConstraint(columnNames = {"post_id", "device_id"})
    }
)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 비로그인 사용자의 경우 null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 비로그인 사용자 기기 식별자, null 허용
    @Column(name = "device_id", length = 255)
    private String deviceId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected PostLike() {}

    // 로그인 사용자 좋아요
    public PostLike(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    // 비로그인 사용자 좋아요
    public PostLike(Post post, String deviceId) {
        this.post = post;
        this.deviceId = deviceId;
    }

    // 로그인 사용자 좋아요 + deviceId 함께 저장 (로그아웃 후 중복 방지용)
    public PostLike(Post post, User user, String deviceId) {
        this.post = post;
        this.user = user;
        this.deviceId = deviceId;
    }

    public Long getId() { return id; }
    public Post getPost() { return post; }
    public User getUser() { return user; }
    public String getDeviceId() { return deviceId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
