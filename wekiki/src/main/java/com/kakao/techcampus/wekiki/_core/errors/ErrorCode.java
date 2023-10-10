package com.kakao.techcampus.wekiki._core.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "WEKIKI-000", "Internal server error"),
    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND,"PAGE-001" , "존재하지 않는 페이지 입니다."),
    PAGE_ALREADY_PRESENT(HttpStatus.BAD_REQUEST,"PAGE-002","이미 존재하는 페이지 입니다."),
    PAGE_HAVE_POST(HttpStatus.BAD_REQUEST,"PAGE-003","글이 적혀있는 페이지는 삭제가 불가능합니다."),
    PARENT_POST_NOT_FOUND(HttpStatus.NOT_FOUND,"POST-001","존재하지 않는 PARENT POST 입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND,"POST-002","존재하지 않는 POST 입니다."),
    POST_SAME_DATE(HttpStatus.BAD_REQUEST,"POST-003","동일한 데이터 입니다.");


    /*
    예시)
    SAME_EMAIL(HttpStatus.CONFLICT, "USER-001", "이미 가입한 이메일입니다.")
    EMAIL_STRUCTURE(HttpStatus.FORBIDDEN,"USER-002","이메일 형식으로 작성해주세요")

    이렇게 여기 정의하고 throw new ApplicationException(ErrorCode.SAME_EMAIL); 던지면 됨
    */


    private HttpStatus status;
    private String errorCode;
    private String message;
}
