package com.myblog.my_dev_blog.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateRequest(
        Long categoryGroupId,   // null이면 기존 그룹 유지
        @NotBlank String name,
        int displayOrder
) {}
