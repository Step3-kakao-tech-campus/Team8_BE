package com.kakao.techcampus.wekiki._core.errors;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApplicationException extends RuntimeException {

    private ErrorCode errorCode;
    private LocalDateTime timestamp;


    public ApplicationException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
}

