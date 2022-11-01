package com.example.pinterestclone.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.example.pinterestclone.controller.request.PostRequestDto;
import com.example.pinterestclone.controller.response.ResponseDto;
import com.example.pinterestclone.service.FileService;
import com.example.pinterestclone.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
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

//    private final S3Uploader s3Uploader;
//
//    @GetMapping("/")
//    public String index() {
//        return "index";
//    }
//
//    @PostMapping("/upload")
//    @ResponseBody
//    public String upload(@RequestParam("data") MultipartFile multipartFile) throws IOException {
//        return s3Uploader.upload(multipartFile, "static");
//    }

//    @SneakyThrows
    @PostMapping("/posts/image")
    public ResponseDto<?> createPostImage(@RequestPart(value = "file", required = false) MultipartFile multipartFile) throws IOException, URISyntaxException {
        return fileService.createPostImage(multipartFile);
    }
// @DeleteMapping( "/posts/images")
//    public void deleteObject(DeleteObjectRequest deleteObjectRequest)
//            throws SdkClientException, AmazonServiceException {
//        fileService.deleteObject(deleteObjectRequest);
//    };
//    public void deleteImage(@PathVariable File file) {
//        fileService.delete(file);
//    }

    //게시글 전체 조회
//    @GetMapping("/posts/all")
//    public ResponseDto<?> getAllPosts() {//(@RequestParam("page") int pageNum)
//        return postService.getAllPost();
//    }
    @GetMapping("/posts")
    public ResponseDto<?> getAllPosts(Pageable pageable) {
        return postService.getAllPost(pageable);
    }

//    @ApiOperation(value = "전체 상품 조회 메소드")
//    @GetMapping("items")
//    public ResponseEntity<?> getAllItem(ItemRequestParam itemRequestParam,
//                                        @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
//        return ResponseEntity.ok().body(itemService.getAllItem(itemRequestParam, pageable));
//    }


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
