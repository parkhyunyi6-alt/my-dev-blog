package com.myblog.my_dev_blog.service;

import com.myblog.my_dev_blog.dto.request.CategoryCreateRequest;
import com.myblog.my_dev_blog.dto.request.CategoryGroupCreateRequest;
import com.myblog.my_dev_blog.dto.request.CategoryGroupUpdateRequest;
import com.myblog.my_dev_blog.dto.request.CategoryUpdateRequest;
import com.myblog.my_dev_blog.dto.response.CategoryGroupResponse;
import com.myblog.my_dev_blog.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryGroupResponse> getAllCategoryGroups();

    CategoryGroupResponse createCategoryGroup(CategoryGroupCreateRequest request);

    CategoryGroupResponse updateCategoryGroup(Long id, CategoryGroupUpdateRequest request);

    void deleteCategoryGroup(Long id);

    CategoryResponse createCategory(CategoryCreateRequest request);

    CategoryResponse updateCategory(Long id, CategoryUpdateRequest request);

    void deleteCategory(Long id);
}
