package com.example.pinterestclone.controller;

import com.example.pinterestclone.controller.request.PostRequestDto;
import com.example.pinterestclone.controller.response.ResponseDto;
import com.example.pinterestclone.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/api/auth/posts")
    public ResponseDto<?> createPosts(@RequestBody PostRequestDto requestDto,
                                      HttpServletRequest request) {
        return postService.createPost(requestDto, request);
    }
}
