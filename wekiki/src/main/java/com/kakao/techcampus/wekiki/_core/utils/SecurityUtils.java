package com.kakao.techcampus.wekiki._core.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String currentMember() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
