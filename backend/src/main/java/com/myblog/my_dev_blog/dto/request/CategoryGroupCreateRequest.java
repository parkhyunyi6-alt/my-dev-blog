package com.myblog.my_dev_blog.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryGroupCreateRequest(
        @NotBlank String name,
        int displayOrder
) {}
