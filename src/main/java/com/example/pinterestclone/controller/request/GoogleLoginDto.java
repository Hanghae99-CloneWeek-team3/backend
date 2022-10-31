package com.example.pinterestclone.controller.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoogleLoginDto {

    private String iss; // ID 토큰을 발급한 인증 기관 정보
    private String azp; // 토큰이 발급된 대상
    private String aud; // 토큰의 사용 대상입니다
    private String sub; // 제목: 요청을 수행하는 주 구성원을 나타내는 ID입니다.
    private String email;
    private String emailVerified;
    private String atHash;
    private String name;
    private String picture;
    private String givenName;
    private String familyName;
    private String locale;
    private String iat; // IssusedAt. 토큰 발행 시간
    private String exp;
    private String alg;
    private String kid; // 토큰 발행 서버의 공개키
    private String typ;

}