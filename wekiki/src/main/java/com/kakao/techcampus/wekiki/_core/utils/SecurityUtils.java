package com.kakao.techcampus.wekiki._core.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long currentMember() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
