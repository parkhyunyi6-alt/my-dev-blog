package com.myblog.my_dev_blog.dto.response;

import com.myblog.my_dev_blog.entity.Post;
import com.myblog.my_dev_blog.entity.PostTag;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        CategoryResponse category,
        List<TagResponse> tags,
        AuthorResponse author,
        int viewCount,
        int heartCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostDetailResponse from(Post post, List<PostTag> postTags) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                CategoryResponse.from(post.getCategory()),
                postTags.stream().map(pt -> TagResponse.from(pt.getTag())).toList(),
                AuthorResponse.from(post.getUser()),
                post.getViewCount(),
                post.getHeartCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
