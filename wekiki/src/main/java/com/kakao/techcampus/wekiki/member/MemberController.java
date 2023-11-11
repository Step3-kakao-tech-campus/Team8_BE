package com.kakao.techcampus.wekiki.member;

import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody MemberRequest.signUpRequestDTO signUpRequestDTO) {
        memberService.signUp(signUpRequestDTO);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody MemberRequest.loginRequestDTO loginDTO) {
        MemberResponse.authTokenDTO response = memberService.login(loginDTO);
        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @GetMapping("/kakao/signin")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code) {
        MemberResponse.authTokenDTO response = memberService.getKakaoInfo(code);
        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @GetMapping("/myinfo")
    public ResponseEntity<?> myPage() {
        MemberResponse.myInfoResponseDTO response = memberService.getMyInfo();
        return ResponseEntity.ok(ApiUtils.success(response));
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
    public ResponseEntity<?> changePassword(@RequestBody @Valid MemberRequest.changePasswordRequestDTO changePasswordRequestDTO) {
        memberService.changePassword(changePasswordRequestDTO);
        return ResponseEntity.ok(true);
    }

    @PatchMapping("/changename")
    public ResponseEntity<?> changeNickName(@RequestBody @Valid MemberRequest.changeNickNameRequestDTO nickNameRequestDTO) {
        memberService.changeNickName(nickNameRequestDTO);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/pusanuniv")
    public ResponseEntity<?> sendMail(@RequestBody @Valid MemberRequest.PNUEmailRequestDTO PNUemailRequestDTO) {
        memberService.sendEmail(PNUemailRequestDTO.getEmail());
        return ResponseEntity.ok(true);
    }

    @PostMapping("/pusanuniv/cert")
    public ResponseEntity<?> checkPNUEmail(@RequestBody MemberRequest.checkPNUEmailRequestDTO pnuEmailRequestDTO) {
        memberService.checkPNUEmail(pnuEmailRequestDTO);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/password/find")
    public ResponseEntity<?> findPassword(@RequestBody MemberRequest.findPasswordRequestDTO findPasswordRequestDTO) {
        memberService.findPassword(findPasswordRequestDTO.getEmail());
        return ResponseEntity.ok(true);
    }



}
