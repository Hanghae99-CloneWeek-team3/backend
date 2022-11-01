package com.example.pinterestclone.service;

import com.example.pinterestclone.controller.handler.CustomError;
import com.example.pinterestclone.controller.handler.CustomException;
import com.example.pinterestclone.controller.request.PostRequestDto;
import com.example.pinterestclone.controller.response.PostResponseDto;
import com.example.pinterestclone.controller.response.ResponseDto;
import com.example.pinterestclone.domain.Post;
import com.example.pinterestclone.domain.Users;
import com.example.pinterestclone.jwt.TokenProvider;
import com.example.pinterestclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;


    @Transactional
    public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request) {

        if (null == request.getHeader("Refresh_Token") || null == request.getHeader("Authorization")) {
            throw new CustomException(CustomError.INVALID_MEMBER);
        }
        Users users = validateUsers(request);
        if (null == users) {
            throw new CustomException(CustomError.INVALID_TOKEN);
        }
        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .imageUrl(requestDto.getImageUrl())
                .users(users)
                .build();

        postRepository.save(post);
        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .author(post.getUsers().getUniqueName())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );
    }


    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    @javax.transaction.Transactional
    public Users validateUsers(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
