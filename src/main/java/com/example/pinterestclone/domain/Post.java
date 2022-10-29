package com.example.pinterestclone.domain;

import com.example.pinterestclone.controller.request.PostRequestDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @JsonManagedReference
    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="users_id", nullable = false)
    private Users users;

    public void update(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
        this.imageUrl = postRequestDto.getImageUrl();
    }


    public boolean validateMember(Users users) {
        return !this.users.equals(users);
    }
}
