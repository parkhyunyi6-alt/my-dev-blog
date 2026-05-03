package com.myblog.my_dev_blog.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryGroupUpdateRequest(
        @NotBlank String name,
        int displayOrder
) {}
