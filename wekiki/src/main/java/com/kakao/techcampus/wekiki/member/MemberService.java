package com.kakao.techcampus.wekiki.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberJPARepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    public void signUp(MemberRequest.signUpDTO signUpDTO) {
        Member member = Member.builder()
                .email(signUpDTO.getEmail())
                .password(passwordEncoder.encode(signUpDTO.getPassword()))
                .name(signUpDTO.getNickName())
                .created_at(LocalDateTime.now())
                .build();
        memberRepository.save(member);
    }
}
