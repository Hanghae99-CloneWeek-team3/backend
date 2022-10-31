package com.example.pinterestclone.controller;

import com.example.pinterestclone.controller.request.PostRequestDto;
import com.example.pinterestclone.controller.response.ResponseDto;
import com.example.pinterestclone.service.FileService;
import com.example.pinterestclone.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    private final FileService fileService;

    //게시글 등록
    @PostMapping("/posts")
    public ResponseDto<?> createPost(@RequestBody PostRequestDto requestDto, HttpServletRequest request) {
        return postService.createPost(requestDto, request);
    }

    //이미지 업로드
//    @SneakyThrows
    @PostMapping("/posts/image")
    public ResponseDto<?> createPostImage(@RequestPart(value = "file", required = false) MultipartFile multipartFile) throws IOException, URISyntaxException {
        return fileService.createPostImage(multipartFile);
    }

    //게시글 전체 조회
    @GetMapping("/posts/all")
    public ResponseDto<?> getAllPosts() {//(@RequestParam("page") int pageNum)
        return postService.getAllPost();
    }

    //게시글 상세 조회
    @GetMapping("/posts/{postId}")
    public ResponseDto<?> getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    //게시글 수정
    @PutMapping("/posts/{postId}")
    public ResponseDto<?> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto requestDto,
                                     HttpServletRequest request) {
        return postService.updatePost(postId, requestDto, request);
    }

    //게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseDto<?> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        return postService.deletePost(postId, request);
    }
}
