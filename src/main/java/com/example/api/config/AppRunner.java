package com.example.api.config;

import com.example.api.entity.Post;
import com.example.api.payload.PostDto;
import com.example.api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class AppRunner implements ApplicationRunner {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (int i = 1; i <= 50; i++) {
            PostDto postDto = new PostDto();
            postDto.setTitle(String.format("Post title %s", i));
            postDto.setDescription(String.format("Post description %s", i));
            postDto.setContent(String.format("Post content %s", i));

            postRepository.save(modelMapper.map(postDto, Post.class));
        }

        System.out.println("Post created successfully");
    }
}
