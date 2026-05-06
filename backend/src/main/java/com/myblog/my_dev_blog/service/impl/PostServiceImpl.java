package com.myblog.my_dev_blog.service.impl;

import com.myblog.my_dev_blog.dto.request.PostCreateRequest;
import com.myblog.my_dev_blog.dto.request.PostUpdateRequest;
import com.myblog.my_dev_blog.dto.response.*;
import com.myblog.my_dev_blog.entity.*;
import com.myblog.my_dev_blog.exception.ConflictException;
import com.myblog.my_dev_blog.exception.ForbiddenException;
import com.myblog.my_dev_blog.exception.NotFoundException;
import com.myblog.my_dev_blog.repository.*;
import com.myblog.my_dev_blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PostLikeRepository postLikeRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedPostResponse getPosts(Long categoryId, int page, int size) {
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Post> postPage;

        if (categoryId != null) {
            Category category = findCategoryOrThrow(categoryId);
            postPage = postRepository.findByCategory(category, pageable);
        } else {
            postPage = postRepository.findAll(pageable);
        }

        List<PostListItemResponse> posts = postPage.getContent().stream()
                .map(post -> PostListItemResponse.from(post, postTagRepository.findByPost(post)))
                .toList();

        return new PagedPostResponse(posts, postPage.getTotalPages(), postPage.getTotalElements(), page);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getLatestPost() {
        Post post = postRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new NotFoundException("포스트가 없습니다."));
        return PostDetailResponse.from(post, postTagRepository.findByPost(post));
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long id) {
        Post post = findPostOrThrow(id);
        return PostDetailResponse.from(post, postTagRepository.findByPost(post));
    }

    @Override
    @Transactional(readOnly = true)
    public PostNeighborsResponse getNeighborPosts(Long postId) {
        Post current = findPostOrThrow(postId);

        // 이후 최대 5개 조회
        List<Post> nextPosts = postRepository.findNextPosts(postId, PageRequest.of(0, 5));
        int prevCount = 5 - nextPosts.size();

        // 이후 글이 5개 미만이면 이전 글로 채움
        List<Post> prevPosts = new ArrayList<>();
        if (prevCount > 0) {
            prevPosts = new ArrayList<>(
                    postRepository.findPrevPosts(postId, PageRequest.of(0, prevCount))
            );
            Collections.reverse(prevPosts); // DESC → ASC 정렬
        }

        List<PostSummaryResponse> result = new ArrayList<>();
        prevPosts.stream().map(p -> new PostSummaryResponse(p.getId(), p.getTitle())).forEach(result::add);
        result.add(new PostSummaryResponse(current.getId(), current.getTitle()));
        nextPosts.stream().map(p -> new PostSummaryResponse(p.getId(), p.getTitle())).forEach(result::add);

        return new PostNeighborsResponse(postId, result);
    }

    @Override
    public PostDetailResponse createPost(PostCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        checkOwner(user, "포스트 작성 권한이 없습니다.");

        Category category = findCategoryOrThrow(request.categoryId());
        Post post = postRepository.save(new Post(user, category, request.title(), request.content()));
        List<PostTag> postTags = saveTags(post, request.tags());

        return PostDetailResponse.from(post, postTags);
    }

    @Override
    public PostDetailResponse updatePost(Long id, PostUpdateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        checkOwner(user, "포스트 수정 권한이 없습니다.");

        Post post = findPostOrThrow(id);
        Category category = findCategoryOrThrow(request.categoryId());
        post.update(category, request.title(), request.content());

        postTagRepository.deleteByPost(post);
        List<PostTag> postTags = saveTags(post, request.tags());

        return PostDetailResponse.from(post, postTags);
    }

    @Override
    public void deletePost(Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        checkOwner(user, "포스트 삭제 권한이 없습니다.");

        Post post = findPostOrThrow(id);
        postTagRepository.deleteByPost(post);
        postLikeRepository.deleteByPost(post);
        postRepository.delete(post);
    }

    private List<PostTag> saveTags(Post post, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) return List.of();

        List<String> normalized = tagNames.stream()
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream().toList();

        if (normalized.size() > 10) {
            throw new IllegalArgumentException("태그는 최대 10개까지 입력 가능합니다.");
        }

        return normalized.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(new Tag(name))))
                .map(tag -> postTagRepository.save(new PostTag(post, tag)))
                .toList();
    }

    @Override
    public void incrementViewCount(Long postId) {
        findPostOrThrow(postId);
        postRepository.incrementViewCount(postId);
    }

    @Override
    public void addLike(Long postId, Long userId, String deviceId) {
        Post post = findPostOrThrow(postId);

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
            if (postLikeRepository.existsByPostAndUser(post, user)) {
                throw new ConflictException("이미 좋아요를 누른 포스트입니다.");
            }
            // 비로그인 상태로 누른 동일 기기 row와 unique 제약 충돌 방지
            if (deviceId != null && !deviceId.isBlank()
                    && postLikeRepository.existsByPostAndDeviceId(post, deviceId)) {
                throw new ConflictException("이미 좋아요를 누른 포스트입니다.");
            }
            postLikeRepository.save(new PostLike(post, user, deviceId));
        } else {
            if (deviceId == null || deviceId.isBlank()) {
                throw new IllegalArgumentException("deviceId는 필수입니다.");
            }
            if (postLikeRepository.existsByPostAndDeviceId(post, deviceId)) {
                throw new ConflictException("이미 좋아요를 누른 포스트입니다.");
            }
            postLikeRepository.save(new PostLike(post, deviceId));
        }

        postRepository.incrementHeartCount(postId);
    }

    @Override
    public void removeLike(Long postId, Long userId, String deviceId) {
        Post post = findPostOrThrow(postId);

        PostLike postLike;
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
            postLike = postLikeRepository.findByPostAndUser(post, user)
                    .orElseThrow(() -> new NotFoundException("좋아요 내역을 찾을 수 없습니다."));
        } else {
            if (deviceId == null || deviceId.isBlank()) {
                throw new IllegalArgumentException("deviceId는 필수입니다.");
            }
            postLike = postLikeRepository.findByPostAndDeviceId(post, deviceId)
                    .orElseThrow(() -> new NotFoundException("좋아요 내역을 찾을 수 없습니다."));
        }

        postLikeRepository.delete(postLike);
        postRepository.decrementHeartCount(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, Long userId, String deviceId) {
        Post post = findPostOrThrow(postId);

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
            return postLikeRepository.existsByPostAndUser(post, user);
        }

        if (deviceId != null && !deviceId.isBlank()) {
            return postLikeRepository.existsByPostAndDeviceId(post, deviceId);
        }

        return false;
    }

    private void checkOwner(User user, String message) {
        if (user.getRole() != User.Role.OWNER) {
            throw new ForbiddenException(message);
        }
    }

    private Post findPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("포스트를 찾을 수 없습니다."));
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다."));
    }
}
