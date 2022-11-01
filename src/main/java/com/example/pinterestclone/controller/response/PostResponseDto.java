package com.example.pinterestclone.controller.response;


import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {

    private Long postId;
    private String title;
    private String content;
//    private String imageUrl;
    private String userId; // 이메일
    private String userName; // 닉네임?
    private String uniqueName;
//    private Data imageFile; //
    private Data profileImage; // 프로필 사진
    private String filePath;
//    private List<CommentResponseDto> comments; //댓글

}
