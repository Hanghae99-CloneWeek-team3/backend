package com.example.pinterestclone.repository;

import com.example.pinterestclone.domain.Files;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<Files, Long> {
    void deleteByUrl(String url);

}
