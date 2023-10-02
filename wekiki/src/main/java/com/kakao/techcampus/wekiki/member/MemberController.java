package com.kakao.techcampus.wekiki.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(MemberRequest.signUpDTO signUpDTO) {
        memberService.signUp(signUpDTO);
        return ResponseEntity.ok(true);
    }


}
