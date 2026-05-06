package com.myblog.my_dev_blog.controller;

import com.myblog.my_dev_blog.dto.response.ApiResponse;
import com.myblog.my_dev_blog.dto.response.UserResponse;
import com.myblog.my_dev_blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getMe(userId)));
    }

    // JWT는 stateless — 로그아웃은 클라이언트가 토큰을 버리면 완료
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
