package com.myblog.my_dev_blog.service;

import com.myblog.my_dev_blog.dto.response.UserResponse;

public interface UserService {

    String loginOrRegister(String googleId, String email, String name, String profileImageUrl);

    UserResponse getMe(Long userId);
}
