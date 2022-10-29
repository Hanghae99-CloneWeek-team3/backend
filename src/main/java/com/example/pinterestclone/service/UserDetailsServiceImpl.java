package com.example.pinterestclone.service;


import com.example.pinterestclone.domain.UserDetailsImpl;
import com.example.pinterestclone.domain.Users;
import com.example.pinterestclone.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UsersRepository usersRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<Users> member = usersRepository.findByEmail(email);
    return member
            .map(UserDetailsImpl::new)
            .orElseThrow(() -> new UsernameNotFoundException("nickname not found"));
  }
}
