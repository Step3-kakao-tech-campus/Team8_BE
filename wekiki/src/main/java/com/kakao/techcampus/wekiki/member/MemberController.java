package com.kakao.techcampus.wekiki.member;

import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/myinfo")
    public ResponseEntity<?> myPage() {
        memberService.getMyInfo();
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> cancel() {
        memberService.cancel();
        return ResponseEntity.ok(true);
    }

    /*
    로그아웃은 추후에 Redis 구현 후 Refresh Token을 지우는 기능만 추가할 예정
     */

    @PatchMapping("/password/change")
    public ResponseEntity<?> changePassword(@RequestBody MemberRequest.changePasswordRequestDTO changePasswordRequestDTO) {
        memberService.changePassword(changePasswordRequestDTO);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/pusanuniv")
    public ResponseEntity<?> sendMail(@RequestBody MemberRequest.emailRequestDTO emailRequestDTO) {
        memberService.sendEmail(emailRequestDTO.getEmail());
        return ResponseEntity.ok(true);
    }



}
