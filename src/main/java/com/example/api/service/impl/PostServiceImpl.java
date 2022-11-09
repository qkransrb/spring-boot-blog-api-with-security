package com.example.api.service.impl;

import com.example.api.entity.Post;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.payload.PostDto;
import com.example.api.payload.PostResponse;
import com.example.api.repository.PostRepository;
import com.example.api.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostDto createPost(PostDto postDto) {
        Post post = postRepository.save(modelMapper.map(postDto, Post.class));

        return modelMapper.map(post, PostDto.class);
    }

    @Transactional(readOnly = true)
    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> posts = postRepository.findAll(pageable);

        List<PostDto> content = posts.getContent()
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());

        return new PostResponse(
                content,
                pageNo,
                pageSize,
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.isFirst(),
                posts.isLast()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));

        return modelMapper.map(post, PostDto.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostDto updatePost(Long id, PostDto postDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        return modelMapper.map(post, PostDto.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));

        System.out.println("post.getId() = " + post.getId());
        System.out.println("post.getTitle() = " + post.getTitle());

        postRepository.delete(post);

        return "Post deleted successfully";
    }
}
