package com.myblog.my_dev_blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostCreateRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull Long categoryId,
        @Size(max = 10) List<String> tags
) {}
