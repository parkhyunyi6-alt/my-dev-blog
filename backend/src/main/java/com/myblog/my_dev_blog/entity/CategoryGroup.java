package com.myblog.my_dev_blog.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "category_groups")
public class CategoryGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected CategoryGroup() {}

    public CategoryGroup(String name, int displayOrder) {
        this.name = name;
        this.displayOrder = displayOrder;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getDisplayOrder() { return displayOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
