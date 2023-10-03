package com.kakao.techcampus.wekiki.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class MemberRequest {
    @Getter
    public static class signUpRequestDTO {
        private String email;
        private String password;
        private String nickName;
    }

    @Getter
    public static class loginRequestDTO {
        private String email;
        private String password;
    }
}
