package com.kakao.techcampus.wekiki.member;

import lombok.Getter;
import lombok.Setter;

public class MemberRequest {
    @Getter
    public static class signUpDTO {
        private String email;
        private String password;
        private String nickName;
    }
}
