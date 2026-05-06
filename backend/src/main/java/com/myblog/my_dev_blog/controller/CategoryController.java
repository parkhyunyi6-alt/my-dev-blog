package com.myblog.my_dev_blog.controller;

import com.myblog.my_dev_blog.dto.request.CategoryCreateRequest;
import com.myblog.my_dev_blog.dto.request.CategoryGroupCreateRequest;
import com.myblog.my_dev_blog.dto.request.CategoryGroupUpdateRequest;
import com.myblog.my_dev_blog.dto.request.CategoryUpdateRequest;
import com.myblog.my_dev_blog.dto.response.ApiResponse;
import com.myblog.my_dev_blog.dto.response.CategoryGroupResponse;
import com.myblog.my_dev_blog.dto.response.CategoryResponse;
import com.myblog.my_dev_blog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/api/category-groups")
    public ResponseEntity<ApiResponse<List<CategoryGroupResponse>>> getAllCategoryGroups() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategoryGroups()));
    }

    @PostMapping("/api/category-groups")
    public ResponseEntity<ApiResponse<CategoryGroupResponse>> createCategoryGroup(
            @RequestBody @Valid CategoryGroupCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(categoryService.createCategoryGroup(request)));
    }

    @PutMapping("/api/category-groups/{id}")
    public ResponseEntity<ApiResponse<CategoryGroupResponse>> updateCategoryGroup(
            @PathVariable Long id,
            @RequestBody @Valid CategoryGroupUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.updateCategoryGroup(id, request)));
    }

    @DeleteMapping("/api/category-groups/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategoryGroup(@PathVariable Long id) {
        categoryService.deleteCategoryGroup(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/api/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestBody @Valid CategoryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(categoryService.createCategory(request)));
    }

    @PutMapping("/api/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.updateCategory(id, request)));
    }

    @DeleteMapping("/api/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
