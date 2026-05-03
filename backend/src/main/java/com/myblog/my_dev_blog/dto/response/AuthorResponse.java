package com.myblog.my_dev_blog.dto.response;

import com.myblog.my_dev_blog.entity.User;

public record AuthorResponse(Long id, String name, String profileImageUrl) {

    public static AuthorResponse from(User user) {
        return new AuthorResponse(user.getId(), user.getName(), user.getProfileImageUrl());
    }
}
