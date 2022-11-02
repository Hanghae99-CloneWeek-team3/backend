package com.example.pinterestclone.controller.response;

import com.example.pinterestclone.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
public class ReCommentResponseDto {


    private List<ReComment> comments = new ArrayList<>();

    public void addComment( int index, Comment comment){
        this.comments.add(new ReCommentResponseDto.ReComment(comment));
    }

    public void addPagination(int size, int totalCounts, int totalPages,
                              int currentPage, Integer nextPage, Boolean isLastPage) {
        this.pagination = new Pagination(size, totalCounts, totalPages,
                currentPage, nextPage, isLastPage);
    }
    private Pagination pagination;
    public int compareTo(CommentResponseDto o) {
        return o.getCreatedAt().compareTo(o.getCreatedAt());
    }

    @Getter
    @Setter
    public class ReComment  implements Comparator<ReComment> {

        private Long commentId;
        private Long parentId;
        private Long postId;
        private String parentName;
        private Long userId;
        private String userName;
        private String uniqueName;
        private String content;
        private String profileImage;
        private int likes;
        private String redHeart;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        private Pagination pagination;
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

        @Override
        public int compare(ReComment o1, ReComment o2) {
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
