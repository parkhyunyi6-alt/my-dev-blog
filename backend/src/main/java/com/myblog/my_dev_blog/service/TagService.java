package com.myblog.my_dev_blog.service;

import com.myblog.my_dev_blog.dto.response.TaggedPostsResponse;
import com.myblog.my_dev_blog.dto.response.TagResponse;

import java.util.List;

public interface TagService {

    List<TagResponse> getAllTags();

    TaggedPostsResponse getPostsByTagId(Long tagId, int page, int size);
}
