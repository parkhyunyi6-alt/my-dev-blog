package com.myblog.my_dev_blog.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    // post_likes 수와 동기화되는 캐싱 컬럼
    @Column(name = "heart_count", nullable = false)
    private int heartCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Post() {}

    public Post(User user, Category category, String title, String content) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Category getCategory() { return category; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getViewCount() { return viewCount; }
    public int getHeartCount() { return heartCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void update(Category category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementHeartCount() {
        this.heartCount++;
    }

    public void decrementHeartCount() {
        if (this.heartCount > 0) {
            this.heartCount--;
        }
    }
}
