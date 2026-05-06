package com.myblog.my_dev_blog.controller;

import com.myblog.my_dev_blog.dto.request.LikeRequest;
import com.myblog.my_dev_blog.dto.request.PostCreateRequest;
import com.myblog.my_dev_blog.dto.request.PostUpdateRequest;
import com.myblog.my_dev_blog.dto.response.*;
import com.myblog.my_dev_blog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedPostResponse>> getPosts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(postService.getPosts(categoryId, page, size)));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getLatestPost() {
        return ResponseEntity.ok(ApiResponse.success(postService.getLatestPost()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(postService.getPost(id)));
    }

    @GetMapping("/{id}/neighbors")
    public ResponseEntity<ApiResponse<PostNeighborsResponse>> getNeighborPosts(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(postService.getNeighborPosts(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPost(
            @RequestBody @Valid PostCreateRequest request,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(postService.createPost(request, userId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> updatePost(
            @PathVariable Long id,
            @RequestBody @Valid PostUpdateRequest request,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.success(postService.updatePost(id, request, userId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        postService.deletePost(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/views")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(@PathVariable Long id) {
        postService.incrementViewCount(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<ApiResponse<Void>> addLike(
            @PathVariable Long id,
            @RequestBody @Valid LikeRequest request,
            @AuthenticationPrincipal Long userId) {
        postService.addLike(id, userId, request.deviceId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<ApiResponse<Void>> removeLike(
            @PathVariable Long id,
            @RequestBody @Valid LikeRequest request,
            @AuthenticationPrincipal Long userId) {
        postService.removeLike(id, userId, request.deviceId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}/likes/status")
    public ResponseEntity<ApiResponse<LikeStatusResponse>> isLiked(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String deviceId) {
        return ResponseEntity.ok(ApiResponse.success(
                new LikeStatusResponse(postService.isLiked(id, userId, deviceId))));
    }
}
