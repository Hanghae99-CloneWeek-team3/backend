package com.example.pinterestclone.controller;

import com.example.pinterestclone.controller.request.LoginRequestDto;
import com.example.pinterestclone.controller.request.UsersRequestDto;
import com.example.pinterestclone.controller.response.ResponseDto;
import com.example.pinterestclone.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UsersController {

    private final UsersService usersService;

    // 회원가입
    @PostMapping(value = "/user/signup")
    public ResponseDto<?> signup(@Valid @RequestBody UsersRequestDto usersRequestDto){
        return usersService.createMember(usersRequestDto);
    }


    // 로그인
    @PostMapping(value = "/user/login")
    public ResponseDto<?> signin(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        return usersService.signin(loginRequestDto, response);
    }

    // 로그아웃
    @PostMapping(value = "/user/signout")
    public ResponseDto<?> signout(HttpServletRequest request){
        return usersService.signout(request);
    }

    @PostMapping(value = "/user/reissue")
    public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return usersService.reissue(request, response);
    }

}


