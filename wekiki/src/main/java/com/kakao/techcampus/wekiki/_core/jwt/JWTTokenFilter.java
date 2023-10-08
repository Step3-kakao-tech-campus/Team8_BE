package com.kakao.techcampus.wekiki._core.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JWTTokenFilter extends OncePerRequestFilter {
    private final JWTTokenProvider jwtTokenProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 authorization 부분을 가져옴
        String jwtHeader = request.getHeader("Authorization");
        // 해당 부분이 null 이거나 Bearer로 시작하지 않으면 리턴
        if(jwtHeader == null || !jwtHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // 위에서 Bearer로 시작하는 것을 알았으니, Bearer을 지워주고, 암호화된 토큰만을 남김
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        // 인증을 거친 뒤, 유저의 정보를 SecurityContextHolder에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug(authentication.getName() + "가 로그인 하였습니다.");
        filterChain.doFilter(request, response);
    }
}
