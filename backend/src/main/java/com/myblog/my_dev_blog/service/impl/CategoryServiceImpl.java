package com.myblog.my_dev_blog.service.impl;

import com.myblog.my_dev_blog.dto.request.CategoryCreateRequest;
import com.myblog.my_dev_blog.dto.request.CategoryGroupCreateRequest;
import com.myblog.my_dev_blog.dto.request.CategoryGroupUpdateRequest;
import com.myblog.my_dev_blog.dto.request.CategoryUpdateRequest;
import com.myblog.my_dev_blog.dto.response.CategoryGroupResponse;
import com.myblog.my_dev_blog.dto.response.CategoryResponse;
import com.myblog.my_dev_blog.entity.Category;
import com.myblog.my_dev_blog.entity.CategoryGroup;
import com.myblog.my_dev_blog.exception.ConflictException;
import com.myblog.my_dev_blog.exception.NotFoundException;
import com.myblog.my_dev_blog.repository.CategoryGroupRepository;
import com.myblog.my_dev_blog.repository.CategoryRepository;
import com.myblog.my_dev_blog.repository.PostRepository;
import com.myblog.my_dev_blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryGroupRepository categoryGroupRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryGroupResponse> getAllCategoryGroups() {
        return categoryGroupRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(group -> {
                    List<Category> categories =
                            categoryRepository.findByCategoryGroupOrderByDisplayOrderAsc(group);
                    return CategoryGroupResponse.from(group, categories);
                })
                .toList();
    }

    @Override
    public CategoryGroupResponse createCategoryGroup(CategoryGroupCreateRequest request) {
        CategoryGroup group = categoryGroupRepository.save(
                new CategoryGroup(request.name(), request.displayOrder())
        );
        return CategoryGroupResponse.from(group, List.of());
    }

    @Override
    public CategoryGroupResponse updateCategoryGroup(Long id, CategoryGroupUpdateRequest request) {
        CategoryGroup group = findGroupOrThrow(id);
        group.updateName(request.name());
        group.updateDisplayOrder(request.displayOrder());
        List<Category> categories = categoryRepository.findByCategoryGroupOrderByDisplayOrderAsc(group);
        return CategoryGroupResponse.from(group, categories);
    }

    @Override
    public void deleteCategoryGroup(Long id) {
        CategoryGroup group = findGroupOrThrow(id);
        if (categoryRepository.existsByCategoryGroup(group)) {
            throw new ConflictException("하위 카테고리가 존재하여 삭제할 수 없습니다.");
        }
        categoryGroupRepository.delete(group);
    }

    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        CategoryGroup group = findGroupOrThrow(request.categoryGroupId());
        Category category = categoryRepository.save(
                new Category(group, request.name(), request.displayOrder())
        );
        return CategoryResponse.from(category);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = findCategoryOrThrow(id);
        category.updateName(request.name());
        category.updateDisplayOrder(request.displayOrder());
        if (request.categoryGroupId() != null) {
            CategoryGroup group = findGroupOrThrow(request.categoryGroupId());
            category.updateCategoryGroup(group);
        }
        return CategoryResponse.from(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = findCategoryOrThrow(id);
        if (postRepository.existsByCategory(category)) {
            throw new ConflictException("해당 카테고리에 포스트가 존재하여 삭제할 수 없습니다.");
        }
        categoryRepository.delete(category);
    }

    private CategoryGroup findGroupOrThrow(Long id) {
        return categoryGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("카테고리 그룹을 찾을 수 없습니다."));
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다."));
    }
}
