package com.myblog.my_dev_blog.dto.response;

import com.myblog.my_dev_blog.entity.Post;
import com.myblog.my_dev_blog.entity.PostTag;

import java.time.LocalDateTime;
import java.util.List;

public record PostListItemResponse(
        Long id,
        String title,
        CategoryResponse category,
        List<TagResponse> tags,
        AuthorResponse author,
        int viewCount,
        int heartCount,
        LocalDateTime createdAt
) {
    public static PostListItemResponse from(Post post, List<PostTag> postTags) {
        return new PostListItemResponse(
                post.getId(),
                post.getTitle(),
                CategoryResponse.from(post.getCategory()),
                postTags.stream().map(pt -> TagResponse.from(pt.getTag())).toList(),
                AuthorResponse.from(post.getUser()),
                post.getViewCount(),
                post.getHeartCount(),
                post.getCreatedAt()
        );
    }
}
