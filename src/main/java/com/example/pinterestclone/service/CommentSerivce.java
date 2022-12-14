package com.example.pinterestclone.service;

import com.example.pinterestclone.controller.handler.CustomError;
import com.example.pinterestclone.controller.handler.CustomException;
import com.example.pinterestclone.controller.request.CommentRequestDto;
import com.example.pinterestclone.controller.request.CommentUpdateRequestDto;
import com.example.pinterestclone.controller.response.CommentListDto;
import com.example.pinterestclone.controller.response.CommentResponseDto;
import com.example.pinterestclone.controller.response.ReCommentResponseDto;
import com.example.pinterestclone.controller.response.ResponseDto;
import com.example.pinterestclone.domain.*;
import com.example.pinterestclone.jwt.TokenProvider;
import com.example.pinterestclone.repository.CommentRepository;
import com.example.pinterestclone.repository.LikesRepository;
import com.example.pinterestclone.repository.PostRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Getter
public class CommentSerivce {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final PostService postService;
    private final TokenProvider tokenProvider;


    @Transactional
    public ResponseDto<?> createComment(CommentRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh_Token") || null == request.getHeader("Authorization")) {
            throw new CustomException(CustomError.MEMBER_NOT_FOUND);
        }


        Users users = validateUsers(request);
        if (null == users) {
            throw new CustomException(CustomError.INVALID_MEMBER);
        }

        //rootname??? post???, post repo?????? rootId(=interview id)??? post??? ??????, ????????? ??????
        //rootname??? ????????????, comment repo?????? rootId(=comment id)??? comment??? ??????, ????????? ??????, ??? ???????????? ???????????? get


        Comment comment = null;
        if (requestDto.getRootName().equals("post")) {
            Post post = postRepository.findById(requestDto.getRootId()).orElseThrow(
                    () -> new CustomException(CustomError.POST_NOT_FOUND));

            log.info("saveComment() >> ?????? ????????? ????????? ID : {}", post.getId());


            comment = new Comment(requestDto,post, users, "false");

            commentRepository.save(comment);

        } else if (requestDto.getRootName().equals("comment")) {
            Comment rootComment = commentRepository.findById(requestDto.getRootId()).orElseThrow(
                    () -> new CustomException(CustomError.COMMENT_NOT_FOUND));
            Post post = rootComment.getPost();

            comment = new Comment(requestDto,post, users, "false");

            commentRepository.save(comment);
        }

        return ResponseDto.success(comment.getId());
    }

    @Transactional
    public ResponseDto<?> updateComment(Long commentId,  CommentUpdateRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh_Token") || null == request.getHeader("Authorization")) {
            throw new CustomException(CustomError.MEMBER_NOT_FOUND);
        }

        Users users = validateUsers(request);
        if (null == users) {
            return ResponseDto.fail(CustomError.WRITER_NOT_MATCHED.name(), //???????????? ????????????
                    CustomError.WRITER_NOT_MATCHED.getMessage());
        }

        Comment comment = isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail(CustomError.COMMENT_NOT_FOUND.name(), //???????????? ?????? ??? ????????????
                    CustomError.COMMENT_NOT_FOUND.getMessage());
        }

        if (comment.validateMember(users)) {
            return ResponseDto.fail(CustomError.WRITER_NOT_MATCHED.name(), //???????????? ????????????
                    CustomError.WRITER_NOT_MATCHED.getMessage());
        }


        comment.update(requestDto.getContents());

        return ResponseDto.success("true");
    }

    @Transactional
    public ResponseDto<?> deleteComment(Long commentId, HttpServletRequest request) {

        if (null == request.getHeader("Refresh_Token") || null == request.getHeader("Authorization")) {
            throw new CustomException(CustomError.MEMBER_NOT_FOUND);
        }

        Users users = validateUsers(request);
        if(null == users) {
            throw new CustomException(CustomError.INVALID_MEMBER);
        }

        Comment comment = isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail(CustomError.COMMENT_NOT_FOUND.name(), //???????????? ?????? ??? ????????????
                    CustomError.COMMENT_NOT_FOUND.getMessage());
        }

        if (comment.validateMember(users)) {
            return ResponseDto.fail(CustomError.WRITER_NOT_MATCHED.name(), //???????????? ????????????
                    CustomError.WRITER_NOT_MATCHED.getMessage());
        }

       /* //?????????????????? ??????????????? ??????
        if (comment.getRootName().equals("interview")){
            List<Comment> childCommentList = commentRepository.findByRootIdAndRootName(comment.getId(), "comment");
            for(Comment childComment: childCommentList){
                commentRepository.deleteById(childComment.getId());
            }
        }*/
        comment.delete();
        commentRepository.deleteById(comment.getId());
        log.info("deleteComment() >> {}??? ????????? ?????? ???????????????", commentId);

        return ResponseDto.success("true");
    }

    public List<CommentListDto.ResponseComment> getAllComments(Long postId, String keyword, int size, int page) {
        Post post = postService.isPresentPost(postId);
        if (null == post) {
            throw new CustomException(CustomError.POST_NOT_FOUND);
        }
        // note that pageable start with 0
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        CommentListDto commentListDto = new CommentListDto();

        // ?????? ??????
        Page<Comment> commentListPage = getListOfCommentOfPost(postId, pageable);
        List<Comment> comments = commentListPage.getContent();

        for( Comment eachComment : comments){
          //  String profileUrl = postService.getProfileImageUrl(eachComment.getUser().getProfileImageUrl());
            commentListDto.addComment(eachComment);
            commentListDto.setRootName(eachComment);
        }

        //??? ???????????? ?????? ????????? ??????, ???????????? ?????????????????? ??? ???????????? ??????????????? ????????? add Nest
        //(-> ????????? ??????, ?????? ????????? ????????? ????????? ?????? ??????????????? ????????? add)
        //??? ???????????? ?????? ????????? ??????


        List<Comment> reCommentList = commentRepository.findAllByRootNameAndRootIdOrderByCreatedAtAsc(postId, keyword, size, page );


        for( Comment eachChild : reCommentList){
            Long itsParentId = eachChild.getPost().getId();

            List<Comment> result = comments.stream()
                    .filter(a -> Objects.equals(a.getPost().getId(), itsParentId))
                    .collect(Collectors.toList());
            log.info("makeCommentList() >> ?????? ?????? ?????? : {}", result);

            //response??? ???????????? ?????????
            List<CommentListDto.ResponseComment> commentListInDto = commentListDto.getComments();
            for (CommentListDto.ResponseComment parentComment: commentListInDto){
                if (parentComment.getCommentId().equals(itsParentId)){
                    int index = commentListDto.getComments().indexOf(parentComment);

                    log.info("makeCommentList() >> ???????????? ???????????? ??????????????? index : {}", index);

                    //String childProfileUrl = interviewService.getProfileImageUrl(eachChild.getUser().getProfileImageUrl());
                    commentListDto.addNestedComment(index, eachChild);
                }
            }
        }
        int totalCounts = commentRepository.countByPostId(postId);
        int totalPages = commentListPage.getTotalPages();
        int totalCountsInThisPage = commentListPage.getNumberOfElements();
        int currentPage = page;
        Boolean isLastPage =  commentListPage.isLast();
        Integer nextPage;

        if (isLastPage){
            nextPage = null;
        }else{
            nextPage = currentPage + 1;
        }

        commentListDto.addPagination(size, totalCountsInThisPage, totalPages, currentPage, nextPage, isLastPage);

        return commentListDto.getComments();
    }

    //??????????????? ????????????
    public List<CommentResponseDto.CommentResponse> getParentComment (Long postId, String rootName, int size, int page){
        CommentResponseDto commentResponseDto = new CommentResponseDto();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        Page<Comment> parentCommentPage = commentRepository.findAllByPostIdAndRootName(postId, rootName, pageable);
        List<Comment> parentComment = parentCommentPage.getContent();
        int a = 0;
        for(int i = 0; i < parentComment.size(); i++){
            commentResponseDto.addComment(parentComment.get(i));
        }


        int totalCounts = commentRepository.countByPostId(postId);
        int totalPages = parentCommentPage.getTotalPages();
        int totalCountsInThisPage = parentCommentPage.getNumberOfElements();
        int currentPage = page;
        Boolean isLastPage =  parentCommentPage.isLast();
        Integer nextPage;

            if (isLastPage){
            nextPage = null;
        }else{
            nextPage = currentPage + 1;
    }

        commentResponseDto.addPagination(size, totalCountsInThisPage, totalPages, currentPage, nextPage, isLastPage);

        return commentResponseDto.getComments();
    }


    public List<ReCommentResponseDto.ReComment> getReCommentList (Long postId, int size, int page, Long commentId) {
        ReCommentResponseDto reCommentResponseDto = new ReCommentResponseDto();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        Page<Comment> reCommentList = commentRepository.findAllReCommentByPostIdAndRootNameLikeComment(postId, "comment", pageable);
        List<Comment> parentComment = commentRepository.findAllByPostIdAndRootNameAndOrderByCreatedAt(postId, "post");
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        int a = 0;
        int b = size * (page + 1);
        for (Comment eachParent : parentComment) {
            commentResponseDto.addComment(eachParent);
        }
        List<CommentResponseDto.CommentResponse> parentComments = commentResponseDto.getComments();

        for (Comment eachComment : reCommentList) {
            Long itsParentId = eachComment.getRootId();
            if(itsParentId == commentId){

                for( CommentResponseDto.CommentResponse parents : parentComments){
                    if (parents.getCommentId().equals(itsParentId)) {
                        int index = commentResponseDto.getComments().indexOf(parents);

                        eachComment.setParentName(parents);

                        reCommentResponseDto.addComment(index, eachComment);
                    }
                } a +=1;
                if(a==b) break;
            }
        }
        return reCommentResponseDto.getComments();
    }

    //????????? ????????? ??????
    public CommentResponseDto getReComment (Long postId, String rootName, int size, int page, Long commentId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        List<Comment> reCommentList = commentRepository.findAllReCommentByPostIdAndRootNameLikeComment(postId, "comment");
        List<Comment> parentComment = commentRepository.findAllByPostIdAndRootNameAndOrderByCreatedAt(postId, "post");
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        int a = 0;
        int b = size * (page + 1);
        for (Comment eachParent : parentComment) {
            commentResponseDto.addComment(eachParent);
        }
        List<CommentResponseDto.CommentResponse> parentComments = commentResponseDto.getComments();

        for (Comment eachComment : reCommentList) {
                Long itsParentId = eachComment.getRootId();
                if(itsParentId == commentId){

                for( CommentResponseDto.CommentResponse parents : parentComments){
                    if (parents.getCommentId().equals(itsParentId)) {
                        int index = commentResponseDto.getComments().indexOf(parents);
                        commentResponseDto.addReCommentImpl(index, eachComment);
                    }
            } a +=1;
                if(a==b) break;
        }
        }
        return commentResponseDto;
    }


    public Page<Comment> getListOfCommentOfPost(Long postId, Pageable pageable)  {
        Page<Comment> commentListPage = commentRepository.findAllByPostIdAndRootName(postId, "post", pageable);
        return commentListPage;
    }

/*
    //????????? ??????
    //????????? ????????? ?????? ???????????? ?????? ??????
    public List<Comment> getListOfCommentOfComment(Long rootId, String keyword) {
        List<Comment> commentList = commentRepository.findAllByRootIdAndRootNameLikeComment(rootId, keyword );
        return commentList;
    }*/


    @Transactional
    public Users validateUsers(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }


    public Boolean addLike(Users users, Long commentId) {

        Comment comment = commentRepository.findById(commentId).orElseThrow();

        //?????? ????????? ??????
        if(isNotAlreadyLike(users, comment)) {
            likesRepository.save(new Likes(comment, users));
            comment.setRedHeart(users, commentId);
            return true;
        }
        return false;
    }

    public Boolean cancelLike(Users users, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        Likes likes = likesRepository.findByUsersAndComment(users, comment).orElseThrow();
        likesRepository.delete(likes);
        comment.cancelRedHeart(users, commentId);
        return true;
    }


    public Integer count(Long commentId, Users loginMember) {

        Comment comment = commentRepository.findById(commentId).orElseThrow();

        Integer commentLikesCount = likesRepository.countByComment(comment).orElse(0);

        return commentLikesCount;
    }


    //???????????? ?????? ????????? ??? ??????????????? ??????
    private boolean isNotAlreadyLike(Users users, Comment comment) {
        return likesRepository.findByUsersAndComment(users, comment).isEmpty();
    }


    public List<CommentResponseDto.CommentResponse> getOneComment (Long id) {

        Comment oneComment = commentRepository.findByCommentId(id);
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.addComment(oneComment);

        return commentResponseDto.getComments();
    }

    @Transactional
    public Comment isPresentComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        return optionalComment.orElse(null);
    }

}
