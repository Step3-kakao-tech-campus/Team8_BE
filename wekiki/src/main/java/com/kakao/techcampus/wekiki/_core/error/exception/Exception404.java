package com.kakao.techcampus.wekiki._core.error.exception;

import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;


// 권한 없음
@Getter
public class Exception404 extends RuntimeException {
    public Exception404(String message) {
        super(message);
    }

    public ApiUtils.ApiResult<?> body(){
        return ApiUtils.error(getMessage());
    }

    public HttpStatus status(){
        return HttpStatus.NOT_FOUND;
    }
}