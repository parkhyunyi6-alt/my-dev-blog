package com.myblog.my_dev_blog.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_group_id", nullable = false)
    private CategoryGroup categoryGroup;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Category() {}

    public Category(CategoryGroup categoryGroup, String name, int displayOrder) {
        this.categoryGroup = categoryGroup;
        this.name = name;
        this.displayOrder = displayOrder;
    }

    public Long getId() { return id; }
    public CategoryGroup getCategoryGroup() { return categoryGroup; }
    public String getName() { return name; }
    public int getDisplayOrder() { return displayOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void updateCategoryGroup(CategoryGroup categoryGroup) {
        this.categoryGroup = categoryGroup;
    }
}
