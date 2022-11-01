package com.example.pinterestclone.controller.response;

import com.example.pinterestclone.domain.Comment;
import com.example.pinterestclone.domain.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentListDto {

    private List<ResponseComment> comments = new ArrayList<>();

    public void addComment(Comment comment){
        this.comments.add(new ResponseComment(comment));
    }
    public void setRootName(Comment comment){
        comment.getRootName();
    }

    public void addNestedComment(int index, Comment comment){
        this.comments.get(index).addNestedCommentWithoutNest(new ReComment(comment));
    }
    private Pagination pagination;

    public void addPagination(int size, int totalCounts, int totalPages,
                              int currentPage, Integer nextPage, Boolean isLastPage) {
        this.pagination = new Pagination(size, totalCounts, totalPages,
                currentPage, nextPage, isLastPage);
    }
    @Getter
    @Setter
    public class ResponseComment {
        private Pagination pagination;
        private Long commentId;
        private Long postId;
        @JsonIgnore
        private Users userOrigin;
        private Long userId;
        private String userName;
        private String uniqueName;
        private String profileImage;

        private String content;
        private Boolean redHeart;
        private Integer likes;
        //   private Long nestedCommentsCount;
        private List<ReComment> reComments = new ArrayList<>();
        @JsonIgnore
        private Long reCommentsCount = 0L;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public ResponseComment(Comment comment) {
            this.commentId = comment.getId();
            this.postId = comment.getPost().getId();
            this.userOrigin = comment.getUsers();
            this.userId = userOrigin.getId();
            this.userName = userOrigin.getUserId();
            this.uniqueName = userOrigin.getUniqueName();
            this.profileImage = userOrigin.getProfileImage();
            // userOrigin.getIntroduce());
            this.likes = comment.getLikes().size();
            this.redHeart = comment.getRedHeart();
            this.content = comment.getContent();
            this.createdAt = comment.getCreatedAt();
            this.modifiedAt = comment.getModifiedAt();
            //this.isMine = isMine;
        }

        public void addNestedCommentWithoutNest(ReComment reComment) {
            this.reComments.add(reComment);
            this.reCommentsCount += 1;
        }
    }

        @Getter
        @Setter
        public class ReComment {
            private CommentListDto.Pagination pagination;
            private Long commentId;
            private Long parentId;
            private Long postId;
            private String parentName;
            @JsonIgnore
            private Users userOrigin;
            private Long userId;
            private String userName;
            private String uniqueName;
            private String content;
            private String profileImage;
            private Integer likes;
            private boolean redHeart;
            @JsonIgnore
            private Boolean folded;
            private LocalDateTime createdAt;
            private LocalDateTime modifiedAt;



            public void addPagination(int size, int totalCounts, int totalPages,
                              int currentPage, Integer nextPage, Boolean isLastPage) {
                this.pagination = new CommentListDto.Pagination(size, totalCounts, totalPages,
                currentPage, nextPage, isLastPage);}

            public ReComment(Comment comment) {
                this.commentId = comment.getId();
                this.parentId = comment.getRootId();
                this.postId = comment.getPost().getId();
                this.parentName = comment.getParentName();
                this.userOrigin = comment.getUsers();
                this.userId = userOrigin.getId();
                this.userName = userOrigin.getUserId();
                this.uniqueName = userOrigin.getUniqueName();
                this.profileImage = userOrigin.getProfileImage();
                // userOrigin.getIntroduce());
                this.likes = comment.getLikes().size();
                this.redHeart = comment.getRedHeart();
                this.content = comment.getContent();
                this.createdAt = comment.getCreatedAt();
                this.modifiedAt = comment.getModifiedAt();
                this.parentId = comment.getRootId();
                //this.isMine = isMine;
            }
        }

        @Getter
        @AllArgsConstructor
        public class Pagination {
            private int per;
            private int totalCounts;
            private int totalPages;
            private int currentPage;
            private Integer nextPage;
            private Boolean isLastPage;
        }

}
