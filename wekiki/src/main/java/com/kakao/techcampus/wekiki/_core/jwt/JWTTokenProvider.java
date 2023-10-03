package com.kakao.techcampus.wekiki._core.jwt;

import com.kakao.techcampus.wekiki.member.MemberResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JWTTokenProvider {
    //jwt 토큰 암호화를 위한 키
    private final Key secretKey;
    //Access token의 시간을 15분으로 설정
    private static final long ACCESS_TOKEN_LIFETIME = 15 * 60 * 1000;

    public JWTTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }


    public MemberResponse.AuthTokenDTO generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        // 현재 시간
        long now = (new Date()).getTime();
        //Access 토큰 만료 시간
        Date accessTokenValidTime = new Date(now + ACCESS_TOKEN_LIFETIME);
        //Access 토큰 제작
        String accessToken = Jwts.builder()
                //아이디 주입
                .setSubject(authentication.getName())
                //권한 주입
                .claim("auth", authorities)
                //만료시간 주입
                .setExpiration(accessTokenValidTime)
                //암호화
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return new MemberResponse.AuthTokenDTO("Bearer", accessToken, accessTokenValidTime);
    }
}
