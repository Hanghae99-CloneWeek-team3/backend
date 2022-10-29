package com.example.pinterestclone.repository;


import com.example.pinterestclone.domain.RefreshToken;
import com.example.pinterestclone.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUsers(Users users);

    Optional<RefreshToken> findByValue(String refresh_token);

}
