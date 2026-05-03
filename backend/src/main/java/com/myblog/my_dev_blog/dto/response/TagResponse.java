package com.myblog.my_dev_blog.dto.response;

import com.myblog.my_dev_blog.entity.Tag;

public record TagResponse(Long id, String name) {

    public static TagResponse from(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }
}
