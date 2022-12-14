package com.example.pinterestclone.repository;

import com.example.pinterestclone.domain.Post;
import com.example.pinterestclone.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findALLByOrderByModifiedAtDesc(Pageable pageable);


    List<Post> findByUsers_UniqueName(String uniqueName);


    void deleteById(Long id);




}
