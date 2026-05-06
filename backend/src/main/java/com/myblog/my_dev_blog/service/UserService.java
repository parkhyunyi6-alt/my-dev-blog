package com.myblog.my_dev_blog.service;

import com.myblog.my_dev_blog.dto.response.UserResponse;
import com.myblog.my_dev_blog.entity.User;

public interface UserService {

    User findOrCreateUser(String googleId, String email, String name, String profileImageUrl);

    String loginOrRegister(String googleId, String email, String name, String profileImageUrl);

    UserResponse getMe(Long userId);
}
