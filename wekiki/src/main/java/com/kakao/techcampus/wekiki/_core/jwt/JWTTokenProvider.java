package com.kakao.techcampus.wekiki._core.jwt;

import com.kakao.techcampus.wekiki.member.MemberResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
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


    public MemberResponse.authTokenDTO generateToken(Authentication authentication) {
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

        return new MemberResponse.authTokenDTO("Bearer", accessToken, accessTokenValidTime);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("올바르지 않은 서명의 JWT Token 입니다.", e);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT Token 입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 형식의 JWT Token 입니다.", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT Claims가 비어있습니다.", e);
        }
        return false;
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // 권한 정보가 없으면 예외
        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 Token 입니다.");
        }

        // 토큰 복호화를 통해 받아온 Claim으로 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 Principal, Crendential, Authorities와 함께 생성
        // 이후 Authentication 객체를 반한한다.
        // 이때 유저는 Spring boot 자체 User class
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}
