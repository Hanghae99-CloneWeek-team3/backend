package com.example.pinterestclone.controller.handler;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final CustomError customError;

    public CustomException( CustomError customError){
        super(customError.getMessage());
        this.customError = customError;
    }
}
