package com.myblog.my_dev_blog.service.impl;

import com.myblog.my_dev_blog.dto.response.PostListItemResponse;
import com.myblog.my_dev_blog.dto.response.TagResponse;
import com.myblog.my_dev_blog.dto.response.TaggedPostsResponse;
import com.myblog.my_dev_blog.entity.Post;
import com.myblog.my_dev_blog.entity.Tag;
import com.myblog.my_dev_blog.exception.NotFoundException;
import com.myblog.my_dev_blog.repository.PostRepository;
import com.myblog.my_dev_blog.repository.PostTagRepository;
import com.myblog.my_dev_blog.repository.TagRepository;
import com.myblog.my_dev_blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;

    @Override
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(TagResponse::from)
                .toList();
    }

    @Override
    public TaggedPostsResponse getPostsByTagId(Long tagId, int page, int size) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("태그를 찾을 수 없습니다."));

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findByTagId(tagId, pageable);

        List<PostListItemResponse> posts = postPage.getContent().stream()
                .map(post -> PostListItemResponse.from(post, postTagRepository.findByPost(post)))
                .toList();

        return new TaggedPostsResponse(
                TagResponse.from(tag),
                posts,
                postPage.getTotalPages(),
                postPage.getTotalElements(),
                page
        );
    }
}
