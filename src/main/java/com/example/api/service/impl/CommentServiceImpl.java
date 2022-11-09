package com.example.api.service.impl;

import com.example.api.entity.Comment;
import com.example.api.entity.Post;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.payload.CommentDto;
import com.example.api.repository.CommentRepository;
import com.example.api.repository.PostRepository;
import com.example.api.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(postId)));

        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setPost(post);

        return modelMapper.map(commentRepository.save(comment), CommentDto.class);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments.stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CommentDto getCommentById(Long postId, Long commentId) {
        Comment comment = commentRepository.findByPostIdAndId(postId, commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentId)));

        return modelMapper.map(comment, CommentDto.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findByPostIdAndId(postId, commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentId)));

        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());

        return modelMapper.map(comment, CommentDto.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String deleteComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findByPostIdAndId(postId, commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentId)));

        commentRepository.delete(comment);

        return "Comment deleted successfully";
    }
}
