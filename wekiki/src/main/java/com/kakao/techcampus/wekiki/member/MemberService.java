package com.kakao.techcampus.wekiki.member;

import com.kakao.techcampus.wekiki._core.error.exception.*;
import com.kakao.techcampus.wekiki._core.jwt.JWTTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kakao.techcampus.wekiki._core.utils.SecurityUtils.currentMember;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberJPARepository memberRepository;
    private final JWTTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;

    public void signUp(MemberRequest.signUpRequestDTO signUpRequestDTO) {
        Member member = Member.builder()
                .email(signUpRequestDTO.getEmail())
                .password(passwordEncoder.encode(signUpRequestDTO.getPassword()))
                .name(signUpRequestDTO.getNickName())
                .created_at(LocalDateTime.now())
                .authority(Authority.user)
                .build();
        memberRepository.save(member);
    }

    public MemberResponse.authTokenDTO login(MemberRequest.loginRequestDTO loginDTO) {
        Optional<Member> loginMember = memberRepository.findByEmail(loginDTO.getEmail());
        if(loginMember.isEmpty())
            throw new Exception404("존재하지 않는 이메일입니다.");
        if(!passwordEncoder.matches(loginDTO.getPassword(), loginMember.get().getPassword())){
            throw new Exception400("비밀번호가 틀렸습니다.");
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());
        AuthenticationManager manager = authenticationManagerBuilder.getObject();
        Authentication authentication = manager.authenticate(usernamePasswordAuthenticationToken);
        return tokenProvider.generateToken(authentication);
    }

    public void getMyInfo() {
        Optional<Member> member = memberRepository.findByEmail(currentMember());
        if(member.isEmpty())
            throw new Exception404("없는 회원입니다.");
        // member 안의 groupMember를 뽑아낸 뒤, 그걸로 Response를 제작
        //MemberResponse.myInfoResponseDTO.myInfoGroupDTO()
        //MemberResponse.myInfoResponseDTO(member.get().getName(), )
    }

    public void cancel() {
        Optional<Member> member = memberRepository.findByEmail(currentMember());
        if(member.isEmpty())
            throw new Exception404("없는 회원입니다.");
        memberRepository.delete(member.get());
    }

    public void changePassword(MemberRequest.changePasswordRequestDTO changePasswordDTO) {
        Optional<Member> member = memberRepository.findByEmail(currentMember());
        if(member.isEmpty())
            throw new Exception404("없는 회원입니다.");
        if(!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), member.get().getPassword()))
            throw new Exception400("비밀번호가 틀렸습니다.");
        member.get().changePassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
    }
}
