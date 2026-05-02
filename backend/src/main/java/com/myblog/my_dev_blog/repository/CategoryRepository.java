package com.myblog.my_dev_blog.repository;

import com.myblog.my_dev_blog.entity.Category;
import com.myblog.my_dev_blog.entity.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByCategoryGroupOrderByDisplayOrderAsc(CategoryGroup categoryGroup);

    boolean existsByCategoryGroup(CategoryGroup categoryGroup);
}
