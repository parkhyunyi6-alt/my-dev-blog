package com.myblog.my_dev_blog.dto.response;

import java.util.List;

public record PostNeighborsResponse(Long currentPostId, List<PostSummaryResponse> posts) {}
