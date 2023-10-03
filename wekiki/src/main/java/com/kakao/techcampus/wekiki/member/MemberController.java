package com.kakao.techcampus.wekiki.member;

import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody MemberRequest.signUpRequestDTO signUpRequestDTO) {
        memberService.signUp(signUpRequestDTO);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody MemberRequest.loginRequestDTO loginDTO) {
        MemberResponse.authTokenDTO response = memberService.login(loginDTO);
        return ResponseEntity.ok(ApiUtils.success(response));
    }


}
