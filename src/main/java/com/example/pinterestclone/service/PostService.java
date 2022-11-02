package com.example.pinterestclone.service;

import com.example.pinterestclone.controller.handler.CustomError;

//import com.example.pinterestclone.controller.request.FileRequestDto;
import com.example.pinterestclone.controller.request.PostPutRequestDto;
import com.example.pinterestclone.controller.request.PostRequestDto;
import com.example.pinterestclone.controller.response.FileResponseDto;
import com.example.pinterestclone.controller.response.PostResponseDto;
import com.example.pinterestclone.controller.response.ResponseDto;
import com.example.pinterestclone.domain.Files;
import com.example.pinterestclone.domain.Post;
import com.example.pinterestclone.domain.Users;
import com.example.pinterestclone.jwt.TokenProvider;
import com.example.pinterestclone.repository.FileRepository;
import com.example.pinterestclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;


    private final FileRepository fileRepository;

    private final FileService fileService;

    private final TokenProvider tokenProvider;

    private FileResponseDto fileResponseDto;

    //게시글 생성
    @Transactional
    public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh_Token") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }

        Users users = validateUsers(request);
        if (null == users) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                    CustomError.INVALID_TOKEN.getMessage());
        }
        Files files = fileRepository.findById(requestDto.getFileId()).orElseThrow(() -> new IllegalArgumentException("no file"));
        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .imageUrl(requestDto.getImageUrl())
                .users(users)
                .files(files)

                .build();
        postRepository.save(post);

        return ResponseDto.success("작성 완료"
//                PostResponseDto.builder()
//                        .postId(post.getId())
//                        .title(post.getTitle())
//                        .content(post.getContent())
//                        .profileimage //프로필 이미지
//                        .userName(post.getUsers().getUniqueName()) //
//                        .uniqueName(post.getUsers().getUniqueName()) //@아이디 느낌
//                        .filePath(post.getImageUrl()) //게시글 사진
//                        .comments(commentResponseDtoList) //댓글

//                        .build()
        );
    }

    //게시글 전체 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost(Pageable pageable) {
        Page<Post> postList = postRepository.findALLByOrderByModifiedAtDesc(pageable);
//        List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for (Post post : postList) {
//            int comments = commentRepository.countAllByPost(post); //댓글
            postResponseDtoList.add(
                    PostResponseDto.builder()
                            .postId(post.getId())
                            .filePath(post.getImageUrl())
                            .userName(post.getUsers().getUserId())
                            .uniqueName(post.getUsers().getUniqueName())

//                            .comments(post.getComments())
                            .build()
            );
        }

        return ResponseDto.success(postResponseDtoList);
    }

    //게시글 상세 조회
    @Transactional
    public ResponseDto<?> getPost(Long postId) {
        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail(CustomError.POST_NOT_FOUND.name(),
                    CustomError.POST_NOT_FOUND.getMessage());
        }

        return ResponseDto.success(
                PostResponseDto.builder()
                        .postId(post.getId()) //게시글 id
                        .title(post.getTitle()) //제목
                        .content(post.getContent()) //내용
//                        .profileimage(post.) //프로필이미지
                        .userId(post.getUsers().getEmail())
                        .userName(post.getUsers().getUserId())
                        .uniqueName(post.getUsers().getUniqueName()) //@아이디 느낌
                        .filePath(post.getImageUrl()) //게시글 사진
//                        .comments(commentResponseDtoList) //댓글
                        .build()
        );

    }

    //게시글 수정
    @Transactional
    public ResponseDto<?> updatePost(Long postId, PostPutRequestDto requestDto, HttpServletRequest request) {
        Users users = validateUsers(request);
        if (null == users) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(), //유효한 토큰이 아닙니다
                    CustomError.INVALID_TOKEN.getMessage());
        }

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail(CustomError.POST_NOT_FOUND.name(), //게시글을 찾을 수 없습니다
                    CustomError.POST_NOT_FOUND.getMessage());
        }

        if (post.validateMember(users)) {
            return ResponseDto.fail(CustomError.WRITER_NOT_MATCHED.name(), //작성자가 아닙니다
                    CustomError.WRITER_NOT_MATCHED.getMessage());
        }

        post.update(requestDto);
        return ResponseDto.success("게시글 수정 완료");
    }

    //게시글 삭제
    @Transactional
    public ResponseDto<?> deletePost(Long postId, HttpServletRequest request) {
        Users users = validateUsers(request);
        if (null == users) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                    CustomError.INVALID_TOKEN.getMessage());
        }
        System.out.println("1");
        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail(CustomError.POST_NOT_FOUND.name(), //게시글을 찾을 수 없습니다
                    CustomError.POST_NOT_FOUND.getMessage());
        }
        System.out.println(post.getFiles().getId());
        System.out.println("2");
        if (post.validateMember(users)) {
            return ResponseDto.fail(CustomError.WRITER_NOT_MATCHED.name(), //작성자가 아닙니다
                    CustomError.WRITER_NOT_MATCHED.getMessage());
        }
        // String url = post.getImageUrl();
//        String deleteUrl = imageUrl.substring(imageUrl.indexOf("/")); //이미지
        //s3에서 이미지 삭제
//        fileRepository.deleteByUrl(url);
        System.out.println("3");
//        fileRepository.deleteById(post.getFiles().getId());
        fileRepository.deleteById(post.getFiles().getId());
        System.out.println("4");
//        postRepository.delete(post);
        System.out.println("5");
        postRepository.deleteById(postId);
        return ResponseDto.success("delete success");
    }

    // 특정 유저 게시글 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getUserPost(String uniqueName){
        List<Post> list = postRepository.findByUsers_UniqueName(uniqueName);
        return ResponseDto.success(list);

    }

    @Transactional
    public Users validateUsers(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("refresh_token"))) {

            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }


    @Transactional(readOnly = true)
    public Post isPresentPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElse(null);
    }



}
