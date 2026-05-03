package com.myblog.my_dev_blog.dto.response;

import com.myblog.my_dev_blog.entity.User;

public record UserResponse(
        Long id,
        String email,
        String name,
        String profileImageUrl,
        String role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfileImageUrl(),
                user.getRole().name()
        );
    }
}
