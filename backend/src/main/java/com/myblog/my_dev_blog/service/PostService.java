package com.myblog.my_dev_blog.service;

import com.myblog.my_dev_blog.dto.request.PostCreateRequest;
import com.myblog.my_dev_blog.dto.request.PostUpdateRequest;
import com.myblog.my_dev_blog.dto.response.PagedPostResponse;
import com.myblog.my_dev_blog.dto.response.PostDetailResponse;
import com.myblog.my_dev_blog.dto.response.PostNeighborsResponse;

public interface PostService {

    PagedPostResponse getPosts(Long categoryId, int page, int size);

    PostDetailResponse getLatestPost();

    PostDetailResponse getPost(Long id);

    PostNeighborsResponse getNeighborPosts(Long postId);

    PostDetailResponse createPost(PostCreateRequest request, Long userId);

    PostDetailResponse updatePost(Long id, PostUpdateRequest request, Long userId);

    void deletePost(Long id, Long userId);
}
