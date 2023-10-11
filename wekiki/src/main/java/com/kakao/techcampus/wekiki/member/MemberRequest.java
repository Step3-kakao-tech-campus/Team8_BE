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

    @Getter
    public static class changePasswordRequestDTO {
        private String currentPassword;
        private String newPassword;
    }

    @Getter
    public static class PNUEmailRequestDTO {
        private String email;
    }

    @Getter
    public static class checkPNUEmailRequestDTO {
        private String email;
        private String certificationNumber;
    }

    @Getter
    public static class findPasswordRequestDTO {
        private String email;
    }
}
