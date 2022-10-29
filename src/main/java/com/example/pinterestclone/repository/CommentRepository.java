package com.example.pinterestclone.repository;

import com.example.pinterestclone.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
