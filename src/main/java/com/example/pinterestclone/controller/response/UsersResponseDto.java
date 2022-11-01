package com.example.pinterestclone.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsersResponseDto {
    private String userName;
    private String uniqueName;
    //private String password;
}
