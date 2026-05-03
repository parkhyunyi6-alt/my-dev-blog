package com.myblog.my_dev_blog.dto.response;

import com.myblog.my_dev_blog.entity.Category;

public record CategoryResponse(Long id, String name, int displayOrder) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDisplayOrder());
    }
}
