package com.example.pinterestclone.jwt;


import com.example.pinterestclone.controller.handler.CustomError;
import com.example.pinterestclone.controller.request.TokenDto;
import com.example.pinterestclone.controller.response.ResponseDto;
import com.example.pinterestclone.domain.RefreshToken;
import com.example.pinterestclone.domain.UserDetailsImpl;
import com.example.pinterestclone.domain.Users;
import com.example.pinterestclone.repository.RefreshTokenRepository;
import com.example.pinterestclone.service.UserDetailsServiceImpl;
import com.example.pinterestclone.shared.Authority;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

  private static final String AUTHORITIES_KEY = "auth";
  private static final String BEARER_TYPE = "bearer ";
  private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            //30분
  private static final long REFRESH_TOKEN_EXPRIRE_TIME = 1000 * 60 * 60 * 24 * 7;     //7일

  private final Key key;
  @Autowired
  private final RefreshTokenRepository refreshTokenRepository;
  @Autowired
  private final UserDetailsServiceImpl userDetailsService;

  //@Autowired
  //private final TokenProvider tokenProvider;

  public TokenProvider(@Value("${jwt.secret}") String secretKey,
                       RefreshTokenRepository refreshTokenRepository, UserDetailsServiceImpl userDetailsService) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.userDetailsService = userDetailsService;
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }



  public TokenDto generateTokenDto(Authentication authentication) {
    String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

    long now = (new Date().getTime());

    Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
    String accessToken = Jwts.builder()
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

    String refreshToken = Jwts.builder()
            //.setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .setExpiration(new Date(now + REFRESH_TOKEN_EXPRIRE_TIME))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

    Users users = ((UserDetailsImpl) authentication.getPrincipal()).getUsers();

    RefreshToken refreshTokenObject = RefreshToken.builder()
            .id(users.getId())
            .users(users)
            .value(refreshToken)
            .build();

    refreshTokenRepository.save(refreshTokenObject);

    return TokenDto.builder()
            .grantType(BEARER_TYPE)
            .accessToken(accessToken)
            .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
            .refreshToken(refreshToken)
            .build();

  }

  //Refresh_Token 으로 Access_Token 재발급
  @Transactional
  public TokenDto generateTokenDto(Users users){


    long now = (new Date().getTime());

    Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
    String accessToken = Jwts.builder()
            .setSubject(users.getEmail())
            .claim(AUTHORITIES_KEY, Authority.ROLE_MEMBER)
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

    String refreshToken = Jwts.builder()
            .setExpiration(new Date(now + REFRESH_TOKEN_EXPRIRE_TIME))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

    RefreshToken refreshTokenObject = RefreshToken.builder()
            .id(users.getId())
            .users(users)
            .value(refreshToken)
            .build();

    refreshTokenRepository.save(refreshTokenObject);

    return TokenDto.builder()
            .grantType(BEARER_TYPE)
            .accessToken(accessToken)
            .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
            .refreshToken(refreshToken)
            .build();
  }


  public Authentication getAuthentication(String accessToken) {
    Claims claims = parseClaims(accessToken);

    if (claims.get(AUTHORITIES_KEY) == null) {
      throw new RuntimeException("권한 정보가 없는 토큰 입니다.");
    }

    Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

    UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());

    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }

  public Users getMemberFromAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || AnonymousAuthenticationToken.class.
            isAssignableFrom(authentication.getClass())) {
      return null;
    }
    return ((UserDetailsImpl) authentication.getPrincipal()).getUsers();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
    } catch (ExpiredJwtException e) {
      log.info("Expired JWT token, 만료된 JWT token 입니다.");
    } catch (UnsupportedJwtException e) {
      log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
    } catch (IllegalArgumentException e) {
      log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
    }
    return false;
  }

  private Claims parseClaims(String accessToken) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  @Transactional(readOnly = true)
  public RefreshToken isPresentRefreshToken(Users users) {
    Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUsers(users);
    return optionalRefreshToken.orElse(null);
  }

  @Transactional
  public ResponseDto<?> deleteRefreshToken(Users users) {
    RefreshToken refreshToken = isPresentRefreshToken(users);
    if (null == refreshToken) {
      return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
              CustomError.INVALID_TOKEN.getMessage());
    }

    refreshTokenRepository.delete(refreshToken);
    return ResponseDto.success("success");
  }
}