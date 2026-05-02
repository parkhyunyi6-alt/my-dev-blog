package com.myblog.my_dev_blog.repository;

import com.myblog.my_dev_blog.entity.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Long> {

    List<CategoryGroup> findAllByOrderByDisplayOrderAsc();
}
