package com.example.pinterestclone.repository;

import com.example.pinterestclone.domain.Comment;
import com.example.pinterestclone.domain.Likes;
import com.example.pinterestclone.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUsersAndComment(Users users, Comment comment);

    Optional<Integer> countByComment(Comment comment);

}
