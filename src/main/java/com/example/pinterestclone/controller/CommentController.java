package com.example.pinterestclone.controller;

import com.example.pinterestclone.configuration.LoginUsers;
import com.example.pinterestclone.controller.request.CommentRequestDto;
import com.example.pinterestclone.controller.request.CommentUpdateRequestDto;

import com.example.pinterestclone.controller.response.CommentResponseDto;
import com.example.pinterestclone.controller.response.ReCommentResponseDto;
import com.example.pinterestclone.controller.response.ResponseDto;
import com.example.pinterestclone.domain.Users;
import com.example.pinterestclone.service.CommentSerivce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CommentController {

    private final CommentSerivce commentSerivce;


    @PostMapping(value = "/comments")
    public ResponseDto<?> createComment (@RequestBody CommentRequestDto requestDto, HttpServletRequest request){
        return ResponseDto.success(commentSerivce.createComment(requestDto, request));
    }

    @PutMapping(value = "/comments/{commentId}")
    public ResponseDto<?> updateComment (@PathVariable Long commentId, @RequestBody CommentUpdateRequestDto requestDto,
                                         HttpServletRequest request){
        return ResponseDto.success(commentSerivce.updateComment(commentId, requestDto, request));
    }

    @DeleteMapping(value = "/comments/{commentId}")
    public ResponseDto<?> deleteComment (@PathVariable Long commentId, HttpServletRequest request) {
        return ResponseDto.success(commentSerivce.deleteComment(commentId, request));
    }
/*

    @GetMapping(value = "/comments/{postId}")
    // @Pagable을 통해 보여줄 페이지 위치(0이 시작), 한 페이지에 댓글 개수 2, 정렬 기준(createdAt), 정렬 기준의 순서(오름차순)을 정의
    public CommentListDto getAllComments(@PathVariable Long postId, @PageableDefault(size = 2, sort = "createdAt",
            direction = Sort.Direction.ASC) Pageable pageable) {
        return commentSerivce.getAllComments(postId, pageable);
    }
*/

    @GetMapping(value = "/comments/{rootId}")
    // @Pagable을 통해 보여줄 페이지 위치(0이 시작), 한 페이지에 댓글 개수 2, 정렬 기준(createdAt), 정렬 기준의 순서(오름차순)을 정의
    public ResponseDto<?> getAllComments(@PathVariable Long rootId, String keyword,
                                       int size, int page, Long commentId) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        if (keyword == null) {
            return ResponseDto.success(commentSerivce.getParentComment(rootId, "post", size, page));

        }else {

                List<ReCommentResponseDto.ReCommentResponse> result = commentSerivce.getReCommentList(rootId,  size, page, commentId);

                List<ReCommentResponseDto.ReComment> result = commentSerivce.getReCommentList(rootId,  size, page, commentId);

               /* if (page >= 1) {
                    Stream<CommentResponseDto> result = paging.stream().limit(size + 2 );
                    return ResponseDto.success(result);
                } else {
                    Stream<CommentResponseDto> result = paging.stream().limit(size);*/
                    return ResponseDto.success(result);
                }
    }

    @GetMapping(value = "/comments/find/{commentId}")
    public ResponseDto<?> getOneComment(@PathVariable Long commentId) {
        return ResponseDto.success(commentSerivce.getOneComment(commentId));
    }

    // ------------------------------------------------------------------------------------------------ 권한 필요
    @DeleteMapping("/comments/likes/{commentId}")
    public ResponseDto<?> cancelLike(@LoginUsers Users loginUsers,
                                             @PathVariable Long commentId) {
        if (loginUsers != null) {
            commentSerivce.cancelLike(loginUsers, commentId);
        }
        return ResponseDto.success(true);
    }

    // ------------------------------------------------------------------------------------------------ 권한 필요
    @PostMapping("/comments/likes/{commentId}")
    public ResponseDto<?> addLike(@LoginUsers Users loginUsers,
                                          @PathVariable Long commentId) {
        boolean result = false;

        if (Objects.nonNull(loginUsers))
            result = commentSerivce.addLike(loginUsers, commentId);

        return result ?
                ResponseDto.success(true) : ResponseDto.fail("false", "좋아요를 이미 하셨습니다.");
    }
}

