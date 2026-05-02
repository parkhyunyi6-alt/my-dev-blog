package com.myblog.my_dev_blog.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "google_id", nullable = false, unique = true, length = 255)
    private String googleId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) NOT NULL DEFAULT 'GUEST'")
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum Role {
        OWNER, GUEST
    }

    protected User() {}

    public User(String googleId, String email, String name, String profileImageUrl, Role role) {
        this.googleId = googleId;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.role = (role != null) ? role : Role.GUEST;
    }

    public Long getId() { return id; }
    public String getGoogleId() { return googleId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void updateProfile(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }
}
