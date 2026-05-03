package com.myblog.my_dev_blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryCreateRequest(
        @NotNull Long categoryGroupId,
        @NotBlank String name,
        int displayOrder
) {}
