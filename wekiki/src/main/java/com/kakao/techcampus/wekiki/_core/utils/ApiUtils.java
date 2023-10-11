package com.kakao.techcampus.wekiki._core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class ApiUtils {

    public static <T> ApiResult<T> success(T response) {
        return new ApiResult<>(true, response, null);
    }


    public static <T> ApiResult<T> error(T data) {
        return new ApiResult<>(false, null, data);
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class ApiResult<T> {
        private final boolean success;
        private final T response;
        private final T error;
    }

}
