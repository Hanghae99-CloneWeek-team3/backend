package com.example.pinterestclone.repository;

import com.example.pinterestclone.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {
}
