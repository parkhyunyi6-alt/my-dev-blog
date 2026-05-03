package com.myblog.my_dev_blog.dto.response;

import com.myblog.my_dev_blog.entity.Category;
import com.myblog.my_dev_blog.entity.CategoryGroup;

import java.util.List;

public record CategoryGroupResponse(
        Long id,
        String name,
        int displayOrder,
        List<CategoryResponse> categories
) {
    public static CategoryGroupResponse from(CategoryGroup group, List<Category> categories) {
        return new CategoryGroupResponse(
                group.getId(),
                group.getName(),
                group.getDisplayOrder(),
                categories.stream().map(CategoryResponse::from).toList()
        );
    }
}
