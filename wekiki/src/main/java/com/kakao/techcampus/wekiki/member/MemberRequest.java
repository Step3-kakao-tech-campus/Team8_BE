package com.kakao.techcampus.wekiki.member;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

@AllArgsConstructor
public class MemberRequest {
    @Getter
    public static class signUpRequestDTO {
        @NotNull
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "(계정@도메인.최상위도메인 형식)")
        private String email;
        @NotNull
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}$", message = "(8~20자 영문과 숫자)")
        private String password;
        @NotNull
        @Pattern(regexp = "^[가-힣a-zA-Z]{1,10}$", message = "(1~10자 한글, 영어)")
        private String nickName;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class loginRequestDTO {
        private String email;
        private String password;
    }

    @Getter
    public static class changePasswordRequestDTO {
        private String currentPassword;
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}$", message = "(8~20자 영문과 숫자)")
        private String newPassword;
    }

    @Getter
    public static class changeNickNameRequestDTO {
        @Pattern(regexp = "^[가-힣a-zA-Z]{1,10}$", message = "(1~10자 한글, 영어)")
        private String newNickName;
    }

    @Getter
    public static class PNUEmailRequestDTO {
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@pusan\\.ac\\.kr$")
        private String email;
    }

    @Getter
    public static class checkPNUEmailRequestDTO {
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@pusan\\.ac\\.kr$")
        private String email;
        private String certificationNumber;
    }

    @Getter
    public static class findPasswordRequestDTO {
        private String email;
    }
}
