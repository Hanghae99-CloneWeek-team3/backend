package com.example.pinterestclone.domain;

import com.example.pinterestclone.controller.request.PostPutRequestDto;
import com.example.pinterestclone.controller.request.PostRequestDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Setter
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String imageUrl;

   // @JsonManagedReference
    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    private Boolean reCommentFolded = true;

    private Integer commentCnt;

   // @JsonBackReference
    @ManyToOne
    @JoinColumn(name="users_id", nullable = false)
    private Users users;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="file_id", nullable = true)
    private Files files;


    public void update(PostPutRequestDto postPutRequestDto) {
        this.title = postPutRequestDto.getTitle();
        this.content = postPutRequestDto.getContent();
    }

    public boolean validateMember(Users users) {
        return !this.users.equals(users);
    }
}
