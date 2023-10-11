package com.kakao.techcampus.wekiki._core.error.exception;


import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;


// 인증 안됨
@Getter
public class Exception401 extends RuntimeException {
    public Exception401(String message) {
        super(message);
    }

    public ApiUtils.ApiResult<?> body(){
        return ApiUtils.error(getMessage());
    }

    public HttpStatus status(){
        return HttpStatus.UNAUTHORIZED;
    }
}