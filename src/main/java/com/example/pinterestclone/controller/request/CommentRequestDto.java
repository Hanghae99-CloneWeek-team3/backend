package com.example.pinterestclone.controller.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
        private String contents;
        //댓글이면 postId, 대댓글이면 댓글id
        private Long rootId;
        private String rootName; //comment/post
}
