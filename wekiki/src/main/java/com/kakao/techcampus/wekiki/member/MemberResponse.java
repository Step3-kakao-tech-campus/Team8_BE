package com.kakao.techcampus.wekiki.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

public class MemberResponse {
    //아직 RefreshToken은 안만들었습니다
    @Getter
    @AllArgsConstructor
    public static class AuthTokenDTO {
        private String grantType; // Bearer
        private String accessToken;
        @JsonFormat(timezone = "Asia/Seoul")
        private Date accessTokenValidTime;
    }
}
