package com.example.pinterestclone.repository;

import com.example.pinterestclone.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByModifiedAtDesc(); //수정일자로 내림차순

    Page<Post> findAll(Pageable pageable);

}
