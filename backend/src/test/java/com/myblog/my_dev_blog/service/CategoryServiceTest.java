package com.myblog.my_dev_blog.service;

import com.myblog.my_dev_blog.dto.request.*;
import com.myblog.my_dev_blog.dto.response.CategoryGroupResponse;
import com.myblog.my_dev_blog.dto.response.CategoryResponse;
import com.myblog.my_dev_blog.entity.Category;
import com.myblog.my_dev_blog.entity.CategoryGroup;
import com.myblog.my_dev_blog.exception.ConflictException;
import com.myblog.my_dev_blog.exception.NotFoundException;
import com.myblog.my_dev_blog.repository.CategoryGroupRepository;
import com.myblog.my_dev_blog.repository.CategoryRepository;
import com.myblog.my_dev_blog.repository.PostRepository;
import com.myblog.my_dev_blog.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock CategoryGroupRepository categoryGroupRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock PostRepository postRepository;

    @InjectMocks CategoryServiceImpl categoryService;

    CategoryGroup groupA;
    Category categoryA;

    @BeforeEach
    void setUp() {
        groupA = new CategoryGroup("그룹A", 1);
        ReflectionTestUtils.setField(groupA, "id", 1L);

        categoryA = new Category(groupA, "카테고리A", 1);
        ReflectionTestUtils.setField(categoryA, "id", 10L);
    }

    // ─── getAllCategoryGroups ────────────────────────────────────────────────

    @Nested
    @DisplayName("getAllCategoryGroups")
    class GetAllCategoryGroups {

        @Test
        @DisplayName("displayOrder 오름차순으로 그룹과 하위 카테고리를 반환한다")
        void 전체_조회_성공() {
            given(categoryGroupRepository.findAllByOrderByDisplayOrderAsc()).willReturn(List.of(groupA));
            given(categoryRepository.findByCategoryGroupOrderByDisplayOrderAsc(groupA))
                    .willReturn(List.of(categoryA));

            List<CategoryGroupResponse> result = categoryService.getAllCategoryGroups();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("그룹A");
            assertThat(result.get(0).categories()).hasSize(1);
            assertThat(result.get(0).categories().get(0).name()).isEqualTo("카테고리A");
        }

        @Test
        @DisplayName("그룹이 없으면 빈 리스트를 반환한다")
        void 그룹_없으면_빈_리스트() {
            given(categoryGroupRepository.findAllByOrderByDisplayOrderAsc()).willReturn(List.of());

            List<CategoryGroupResponse> result = categoryService.getAllCategoryGroups();

            assertThat(result).isEmpty();
        }
    }

    // ─── createCategoryGroup ────────────────────────────────────────────────

    @Nested
    @DisplayName("createCategoryGroup")
    class CreateCategoryGroup {

        @Test
        @DisplayName("그룹을 저장하고 빈 카테고리 목록과 함께 반환한다")
        void 그룹_생성_성공() {
            CategoryGroupCreateRequest req = new CategoryGroupCreateRequest("새그룹", 2);
            given(categoryGroupRepository.save(any(CategoryGroup.class))).willReturn(groupA);

            CategoryGroupResponse result = categoryService.createCategoryGroup(req);

            assertThat(result.name()).isEqualTo("그룹A");
            assertThat(result.categories()).isEmpty();
            verify(categoryGroupRepository).save(any(CategoryGroup.class));
        }
    }

    // ─── updateCategoryGroup ────────────────────────────────────────────────

    @Nested
    @DisplayName("updateCategoryGroup")
    class UpdateCategoryGroup {

        @Test
        @DisplayName("이름과 순서를 수정하고 하위 카테고리 목록을 포함해 반환한다")
        void 그룹_수정_성공() {
            CategoryGroupUpdateRequest req = new CategoryGroupUpdateRequest("수정그룹", 3);
            given(categoryGroupRepository.findById(1L)).willReturn(Optional.of(groupA));
            given(categoryRepository.findByCategoryGroupOrderByDisplayOrderAsc(groupA))
                    .willReturn(List.of(categoryA));

            CategoryGroupResponse result = categoryService.updateCategoryGroup(1L, req);

            assertThat(result.name()).isEqualTo("수정그룹");
            assertThat(result.displayOrder()).isEqualTo(3);
            assertThat(result.categories()).hasSize(1);
        }

        @Test
        @DisplayName("그룹이 없으면 NotFoundException")
        void 그룹_없으면_예외() {
            given(categoryGroupRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() ->
                    categoryService.updateCategoryGroup(99L, new CategoryGroupUpdateRequest("x", 0)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("카테고리 그룹");
        }
    }

    // ─── deleteCategoryGroup ────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteCategoryGroup")
    class DeleteCategoryGroup {

        @Test
        @DisplayName("하위 카테고리가 없으면 그룹을 삭제한다")
        void 그룹_삭제_성공() {
            given(categoryGroupRepository.findById(1L)).willReturn(Optional.of(groupA));
            given(categoryRepository.existsByCategoryGroup(groupA)).willReturn(false);

            categoryService.deleteCategoryGroup(1L);

            verify(categoryGroupRepository).delete(groupA);
        }

        @Test
        @DisplayName("하위 카테고리가 있으면 ConflictException")
        void 하위_카테고리_있으면_ConflictException() {
            given(categoryGroupRepository.findById(1L)).willReturn(Optional.of(groupA));
            given(categoryRepository.existsByCategoryGroup(groupA)).willReturn(true);

            assertThatThrownBy(() -> categoryService.deleteCategoryGroup(1L))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("하위 카테고리");

            verify(categoryGroupRepository, never()).delete(any());
        }

        @Test
        @DisplayName("그룹이 없으면 NotFoundException")
        void 그룹_없으면_예외() {
            given(categoryGroupRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.deleteCategoryGroup(99L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ─── createCategory ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("createCategory")
    class CreateCategory {

        @Test
        @DisplayName("그룹 아래에 카테고리를 생성한다")
        void 카테고리_생성_성공() {
            CategoryCreateRequest req = new CategoryCreateRequest(1L, "새카테고리", 2);
            given(categoryGroupRepository.findById(1L)).willReturn(Optional.of(groupA));
            given(categoryRepository.save(any(Category.class))).willReturn(categoryA);

            CategoryResponse result = categoryService.createCategory(req);

            assertThat(result.name()).isEqualTo("카테고리A");
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("그룹이 없으면 NotFoundException")
        void 그룹_없으면_예외() {
            CategoryCreateRequest req = new CategoryCreateRequest(99L, "x", 0);
            given(categoryGroupRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.createCategory(req))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ─── updateCategory ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateCategory")
    class UpdateCategory {

        @Test
        @DisplayName("이름과 순서를 수정한다")
        void 카테고리_이름_순서_수정() {
            CategoryUpdateRequest req = new CategoryUpdateRequest(null, "수정카테고리", 5);
            given(categoryRepository.findById(10L)).willReturn(Optional.of(categoryA));

            CategoryResponse result = categoryService.updateCategory(10L, req);

            assertThat(result.name()).isEqualTo("수정카테고리");
            assertThat(result.displayOrder()).isEqualTo(5);
            verify(categoryGroupRepository, never()).findById(any());
        }

        @Test
        @DisplayName("categoryGroupId가 있으면 소속 그룹을 변경한다")
        void 카테고리_그룹_변경() {
            CategoryGroup newGroup = new CategoryGroup("새그룹", 2);
            ReflectionTestUtils.setField(newGroup, "id", 2L);
            CategoryUpdateRequest req = new CategoryUpdateRequest(2L, "카테고리A", 1);
            given(categoryRepository.findById(10L)).willReturn(Optional.of(categoryA));
            given(categoryGroupRepository.findById(2L)).willReturn(Optional.of(newGroup));

            categoryService.updateCategory(10L, req);

            assertThat(categoryA.getCategoryGroup().getId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("카테고리가 없으면 NotFoundException")
        void 카테고리_없으면_예외() {
            given(categoryRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() ->
                    categoryService.updateCategory(99L, new CategoryUpdateRequest(null, "x", 0)))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ─── deleteCategory ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteCategory")
    class DeleteCategory {

        @Test
        @DisplayName("포스트가 없으면 카테고리를 삭제한다")
        void 카테고리_삭제_성공() {
            given(categoryRepository.findById(10L)).willReturn(Optional.of(categoryA));
            given(postRepository.existsByCategory(categoryA)).willReturn(false);

            categoryService.deleteCategory(10L);

            verify(categoryRepository).delete(categoryA);
        }

        @Test
        @DisplayName("포스트가 존재하면 ConflictException")
        void 포스트_있으면_ConflictException() {
            given(categoryRepository.findById(10L)).willReturn(Optional.of(categoryA));
            given(postRepository.existsByCategory(categoryA)).willReturn(true);

            assertThatThrownBy(() -> categoryService.deleteCategory(10L))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("포스트");

            verify(categoryRepository, never()).delete(any());
        }

        @Test
        @DisplayName("카테고리가 없으면 NotFoundException")
        void 카테고리_없으면_예외() {
            given(categoryRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.deleteCategory(99L))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
