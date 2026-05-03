package com.myblog.my_dev_blog.service.impl;

import com.myblog.my_dev_blog.dto.response.UserResponse;
import com.myblog.my_dev_blog.entity.User;
import com.myblog.my_dev_blog.exception.NotFoundException;
import com.myblog.my_dev_blog.repository.UserRepository;
import com.myblog.my_dev_blog.service.UserService;
import com.myblog.my_dev_blog.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String loginOrRegister(String googleId, String email, String name, String profileImageUrl) {
        User user = userRepository.findByGoogleId(googleId)
                .map(existing -> {
                    existing.updateProfile(name, profileImageUrl);
                    return existing;
                })
                .orElseGet(() -> userRepository.save(
                        new User(googleId, email, name, profileImageUrl, User.Role.GUEST)
                ));

        return jwtTokenProvider.generateToken(user.getId(), user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }
}
