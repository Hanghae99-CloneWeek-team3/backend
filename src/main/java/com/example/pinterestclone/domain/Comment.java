package com.example.pinterestclone.domain;

//import com.example.pinterestclone.controller.request.CommentRequestDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
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
    @JoinColumn(name="post_id", nullable = false)
    private Post post;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.ALL)
    private List<Likes> likes;

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    //OnDelete는 JPA에서는 단일한 DELETE 쿼리만 전송하여 참조하는 레코드들을 연쇄적으로 제거해줌
    //CascadeTypa.REMOVE 방식은 JPA에서 외래 키를 통해 참조하는 레코드들을 제거하기 위해 그 개수만큼 DELETE 쿼리 전송해야함
    // 참고: https://kukekyakya.tistory.com/m/546
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parent;

    @Builder.Default
    // 각 댓글의 하위 댓글을 참조 가능하도록 연관관계 맺음
    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    private boolean hasChildren() {
        return getChildren().size() != 0;
    }

    public boolean validateMember(Users member) {
        return !this.users.equals(member);
    }

//    public void update(CommentRequestDto commentRequestDto) {
//        this.content = commentRequestDto.getContent();
//    }
}
