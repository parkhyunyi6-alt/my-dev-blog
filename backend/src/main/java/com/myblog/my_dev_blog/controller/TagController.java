package com.myblog.my_dev_blog.controller;

import com.myblog.my_dev_blog.dto.response.ApiResponse;
import com.myblog.my_dev_blog.dto.response.TagResponse;
import com.myblog.my_dev_blog.dto.response.TaggedPostsResponse;
import com.myblog.my_dev_blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags() {
        return ResponseEntity.ok(ApiResponse.success(tagService.getAllTags()));
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<ApiResponse<TaggedPostsResponse>> getPostsByTag(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(tagService.getPostsByTagId(id, page, size)));
    }
}
