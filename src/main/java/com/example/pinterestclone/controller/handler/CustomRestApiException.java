package com.example.pinterestclone.controller.handler;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomRestApiException {

    private String field;
    private String errorMessage;

}
