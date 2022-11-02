package com.example.pinterestclone.controller.response;

import com.example.pinterestclone.domain.Comment;
import com.example.pinterestclone.domain.Timestamped;
import com.example.pinterestclone.domain.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
public class CommentResponseDto extends Timestamped {

    private List<CommentResponse> comments = new ArrayList<>();

    public void addComment(Comment comment){
        this.comments.add(new CommentResponse(comment));
    }

    public void addReCommentImpl(int index, Comment comment){
        if(comment == null) {
            System.out.println("comment is null");
        }
        this.comments.get(index).addReComment(new ReComment(comment));
    }

    private Pagination pagination;

    public void addPagination(int size, int totalCounts, int totalPages,
                              int currentPage, Integer nextPage, Boolean isLastPage) {
        this.pagination = new Pagination(size, totalCounts, totalPages,
                currentPage, nextPage, isLastPage);
    }

    public int compareTo(CommentResponseDto o) {
        return o.getCreatedAt().compareTo(o.getCreatedAt());
    }

    @Getter
    public class CommentResponse  implements Comparator<CommentResponse> {
        private Pagination pagination;
        private Long commentId;
        private Long postId;
        private Long userId;
        private String userName;
        private String uniqueName;
        private String profileImage;

        private String content;
        private String redHeart;
        private Integer likes;
        //   private Long nestedCommentsCount;

        private List<ReComment> reComments = new ArrayList<>();

        private Long reCommentsCount = 0L;
        public LocalDateTime createdAt;
        public LocalDateTime modifiedAt;


        public CommentResponse(Comment comment) {
            this.commentId = comment.getId();
            this.postId = comment.getPost().getId();
            this.userId = comment.getUsers().getId();
            this.userName = comment.getUsers().getUserId();
            this.uniqueName = comment.getUsers().getUniqueName();
            this.profileImage = comment.getUsers().getProfileImage();
            // userOrigin.getIntroduce());
            this.likes = comment.getLikes().size();
            this.redHeart = comment.getRedHeart();
            this.content = comment.getContent();
            this.createdAt = comment.getCreatedAt();
            this.modifiedAt = comment.getModifiedAt();
            //this.isMine = isMine;
        }

        public void addReComment(ReComment reComment) {
            this.reComments.add(reComment);
            this.reCommentsCount += 1;
        }

        @Override
        public int compare(CommentResponse o1, CommentResponse o2) {
            Long first = Long.parseLong(String.valueOf(o1.createdAt));
            Long second = Long.parseLong(String.valueOf(o2.createdAt));

            if(first > second){
                return 1;
            }
            else if(first < second){
                return -1;
            }
            return 0;
        }
    }
    @Getter
    @Setter
    public class ReComment {
        private CommentResponseDto.Pagination pagination;
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
        private String redHeart;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private Boolean folded;


        public ReComment(Comment comment) {
            this.commentId = comment.getId();
            this.parentId = comment.getRootId();
            this.postId = comment.getPost().getId();
            this.parentName = comment.getParentName();
            this.userId = comment.getUsers().getId();
            this.userName = comment.getUsers().getUserId();
            this.uniqueName = comment.getUsers().getUniqueName();
            this.profileImage = comment.getUsers().getProfileImage();
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

