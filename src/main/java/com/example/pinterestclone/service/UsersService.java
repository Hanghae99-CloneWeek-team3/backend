package com.example.pinterestclone.service;

import com.example.pinterestclone.controller.handler.CustomError;
import com.example.pinterestclone.controller.request.LoginRequestDto;
import com.example.pinterestclone.controller.request.TokenDto;
import com.example.pinterestclone.controller.request.UsersRequestDto;
import com.example.pinterestclone.controller.response.ResponseDto;
import com.example.pinterestclone.controller.response.UsersResponseDto;
import com.example.pinterestclone.domain.RefreshToken;
import com.example.pinterestclone.domain.Users;
import com.example.pinterestclone.domain.Users;
import com.example.pinterestclone.jwt.TokenProvider;
import com.example.pinterestclone.repository.RefreshTokenRepository;
import com.example.pinterestclone.repository.UsersRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.Random;


@AllArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    @Transactional
    public ResponseDto<?> createMember(UsersRequestDto usersRequestDto) {

        // DB에 중복 이메일이 있는지 확인.
        if(usersRepository.existsByEmail(usersRequestDto.getUserId())){
            return ResponseDto.fail(CustomError.ALREADY_SAVED_LOGINNAME.name(),
                    CustomError.ALREADY_SAVED_LOGINNAME.getMessage());
        }

        String uniqueName = randomUniqueName();
        // 유니크네임이 맞는지 확인~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        String userId = usersRequestDto.getUserId().split("@")[0];

        Users users = Users.builder()
                .userId(userId)
                .uniqueName(uniqueName)
                .email(usersRequestDto.getUserId())
                .password(passwordEncoder.encode(usersRequestDto.getPassword()))
                .build();

        usersRepository.save(users);

        return ResponseDto.success(null);
    }


    @Transactional
    public ResponseDto<?> signin(LoginRequestDto loginRequestDto, HttpServletResponse response) {

        // usersRepository에서 이메일을 기준으로 찾아 없으면 null 있으면 users 반환
        Users users = isPresentUsers(loginRequestDto.getUserId());

        if (null == users) {
            return ResponseDto.fail(CustomError.MEMBER_NOT_FOUND.name(),
                    CustomError.MEMBER_NOT_FOUND.getMessage());
        }

        // 로그인때 입력한 비밀번호와 DB에 있는 비밀번호와 다르다.
        if(!users.validatePassword(passwordEncoder, loginRequestDto.getPassword())){
            return ResponseDto.fail(CustomError.PASSWORDS_NOT_MATCHED.name(),
                    CustomError.PASSWORDS_NOT_MATCHED.getMessage());
        }

        //로그인 할 때 Authentication(인증 객체 생성)
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUserId(), loginRequestDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //Access-Token, Refresh-Token 발급한 후 FE에 ServletResponse로 전달
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        tokenToHeaders(tokenDto, response);

        return ResponseDto.success(
                //null
                UsersResponseDto.builder()
                        .userName(users.getUserId())
                        .uniqueName(users.getUniqueName())
                        .build()
        );

    }

    @Transactional
    public ResponseDto<?> signout(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("refresh_token"))) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                    CustomError.INVALID_TOKEN.getMessage());
        }

        //Authentication에 있는 member 찾기
        Users users = tokenProvider.getMemberFromAuthentication();
        if (null == users) {
            return ResponseDto.fail(CustomError.MEMBER_NOT_FOUND.name(),
                    CustomError.MEMBER_NOT_FOUND.getMessage());
        }

        //Refresh-Token까지 삭제하기
        return tokenProvider.deleteRefreshToken(users);
    }

    public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        //Validity of Refresh-Token not proven => INVALID_TOKEN
        if (!tokenProvider.validateToken(request.getHeader("refresh_token"))) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                    CustomError.INVALID_TOKEN.getMessage());
        }


        Users users = refreshTokenRepository.findByValue(request.getHeader("refresh_token"))
                .map(RefreshToken::getUsers)
                .orElseThrow(() -> new IllegalArgumentException("RefreshToken not found"));

        RefreshToken refreshToken = tokenProvider.isPresentRefreshToken(users);


        if (!refreshToken.getValue().equals(request.getHeader("refresh_token"))) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(), CustomError.INVALID_TOKEN.getMessage());
        }


        TokenDto tokenDto = tokenProvider.generateTokenDto(users);
        refreshToken.updateValue(tokenDto.getRefreshToken());
        tokenToHeaders(tokenDto, response);
        return ResponseDto.success(users);

    }

    @Transactional
    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("refresh_token", tokenDto.getRefreshToken());
        response.addHeader("Access_Token_Expire_Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

    @Transactional(readOnly = true)
    public Users isPresentUsers(String email) {
        Optional<Users> optionalMember = usersRepository.findByEmail(email);
        return optionalMember.orElse(null);
    }

    public String randomUniqueName(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

}
