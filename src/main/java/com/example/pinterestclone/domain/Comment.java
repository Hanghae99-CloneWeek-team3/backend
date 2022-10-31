package com.example.pinterestclone.domain;

//import com.example.pinterestclone.controller.request.CommentRequestDto;
import com.example.pinterestclone.controller.request.CommentRequestDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="users_id", nullable = false)
    private Users users;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Likes> likes;

    @Column
    private boolean readHeart;

    private boolean isDeleted;

    @Column
    private Long rootId;

    @Column
    private String rootName;

    @Column
    private String parentName;


    /*@JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    //OnDelete는 JPA에서는 단일한 DELETE 쿼리만 전송하여 참조하는 레코드들을 연쇄적으로 제거해줌
    //CascadeTypa.REMOVE 방식은 JPA에서 외래 키를 통해 참조하는 레코드들을 제거하기 위해 그 개수만큼 DELETE 쿼리 전송해야함
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parent;

    @Builder.Default
    // 각 댓글의 하위 댓글을 참조 가능하도록 연관관계 맺음
    @OneToMany(mappedBy = "parent")
    private List<Comment> childList = new ArrayList<>();
*/
    public Comment(CommentRequestDto requestDto, Post post) {
        this.content = requestDto.getContents();
        this.rootId = requestDto.getRootId();
        this.rootName = requestDto.getRootName();
        this.post = post;
    }


    //== 수정 ==//
    public void update(String content) {
        this.content = content;
    }
    //== 삭제 ==//
    public void delete() {
        this.isDeleted = true;
    }

    public boolean validateMember(Users users) {
        return !this.users.equals(users);
    }


}
