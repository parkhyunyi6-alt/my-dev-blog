package com.myblog.my_dev_blog.dto.response;

import java.util.List;

public record TaggedPostsResponse(
        TagResponse tag,
        List<PostListItemResponse> posts,
        int totalPages,
        long totalElements,
        int currentPage
) {}
