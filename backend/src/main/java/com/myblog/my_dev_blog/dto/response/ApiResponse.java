package com.myblog.my_dev_blog.dto.response;

public record ApiResponse<T>(boolean success, T data, String message) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "성공");
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
