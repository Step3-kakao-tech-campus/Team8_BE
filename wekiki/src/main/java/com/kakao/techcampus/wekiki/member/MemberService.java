package com.kakao.techcampus.wekiki.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.techcampus.wekiki._core.error.exception.*;
import com.kakao.techcampus.wekiki._core.jwt.JWTTokenProvider;
import com.kakao.techcampus.wekiki._core.utils.RedisUtility;
import com.kakao.techcampus.wekiki._core.utils.redis.RedisUtils;
import com.kakao.techcampus.wekiki.group.domain.GroupMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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
    private final RedisUtility redisUtility;
    private final RedisUtils redisUtils;
    private final JavaMailSender javaMailSender;
    @Value("${kakao.client.id}")
    private String KAKAO_CLIENT_ID;
    @Value("${kakao.redirect.uri}")
    private String KAKAO_REDIRECT_URI;
    @Value("${kakao.client.password")
    private String KAKAO_PASSWORD;
    private static final String PROXY_HOST = "krmp-proxy.9rum.cc";
    private static final int PROXY_PORT = 3128;
    private static final String MEMBER_ID_PREFIX = "member_id:";
    private static final long MEMBER_ID_LIFETIME = 3L;
    private static final long PNU_GROUP_ID = 8L;


    public void signUp(MemberRequest.signUpRequestDTO signUpRequestDTO) {

        Optional<Member> checkMember = memberRepository.findByEmail(signUpRequestDTO.getEmail());
        if (checkMember.isPresent()){
            log.error("이미 존재하는 회원이 가입 요청하였습니다.");
            throw new Exception400("이미 존재하는 회원입니다.");
        }

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
        if(loginMember.isEmpty()) {
            log.error("없는 회원이 로그인 요청을 하였습니다.");
            throw new Exception404("존재하지 않는 이메일입니다.");
        }
        if(!passwordEncoder.matches(loginDTO.getPassword(), loginMember.get().getPassword())){
            log.error("로그인 요청 회원의 비밀번호가 틀렸습니다. User Id : " + loginMember.get().getEmail());
            throw new Exception400("비밀번호가 틀렸습니다.");
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());
        AuthenticationManager manager = authenticationManagerBuilder.getObject();
        Authentication authentication = manager.authenticate(usernamePasswordAuthenticationToken);
        return tokenProvider.generateToken(authentication);
    }

    public MemberResponse.myInfoResponseDTO getMyInfo() {
        Member member;
        try {
            member = findMember();
        } catch (Exception404 e) {
            log.error("Access Token에서 뽑아낸 회원이 존재하지 않는 회원입니다. (마이페이지 조회)");
            throw e;
        }
        List<MemberResponse.myInfoResponseDTO.myInfoGroupDTO> infoGroupDTOList = member.getGroupMembers().stream()
                .filter(GroupMember::isActiveStatus)
                .map(groupMember -> {
                    return new MemberResponse.myInfoResponseDTO.myInfoGroupDTO(groupMember, groupMember.getGroup());
                }).collect(Collectors.toList());
        return new MemberResponse.myInfoResponseDTO(member.getName(), infoGroupDTOList);
    }

    public void cancel() {
        Member member;
        try {
            member = findMember();
        } catch (Exception404 e) {
            log.error("Access Token에서 뽑아낸 회원이 존재하지 않는 회원입니다. (회원 탈퇴)");
            throw e;
        }
        Member cancelMember = new Member("알수없음", "", passwordEncoder.encode(makeRandomPassword()),
                LocalDateTime.now(), Authority.none);
        for (GroupMember g : member.getGroupMembers()) {
            g.update("알수없음");
            g.changeMember(cancelMember);
            g.changeStatus();
        }
        //member.delete(passwordEncoder.encode(makeRandomPassword()));
        memberRepository.delete(member);
    }

    public void changeNickName(MemberRequest.changeNickNameRequestDTO nickNameRequestDTO) {
        Member member;
        try {
            member = findMember();
        } catch (Exception404 e) {
            log.error("Access Token에서 뽑아낸 회원이 존재하지 않는 회원입니다. (전체 닉네임 변경)");
            throw e;
        }
        member.changeNickName(nickNameRequestDTO.getNewNickName());
    }

    public void changePassword(MemberRequest.changePasswordRequestDTO changePasswordDTO) {
        Member member;
        try {
            member = findMember();
        } catch (Exception404 e) {
            log.error("Access Token에서 뽑아낸 회원이 존재하지 않는 회원입니다. (비밀번호 변경)");
            throw e;
        }
        if(!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), member.getPassword())) {
            log.error("비밀번호 변경 요청에서의 비밀번호 확인이 틀렸습니다. User Id : " + member.getEmail());
            throw new Exception400("비밀번호가 틀렸습니다.");
        }
        member.changePassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
    }

    public void sendEmail(String email) {
        Member member;
        try {
            member = findMember();
        } catch (Exception404 e) {
            log.error("Access Token에서 뽑아낸 회원이 존재하지 않는 회원입니다. (부산대 인증 메일 전송)");
            throw e;
        }
        String authNumber = Integer.toString(makeEmailAuthNum());
        redisUtility.setValues(email, authNumber, 300);
        /*SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("부산대 인증 메일입니다!");
        simpleMailMessage.setText("인증 번호 입니다.\n"
                + authNumber +
                "\n잘 입력해 보세요!");
        // 이메일 발신
        javaMailSender.send(simpleMailMessage);*/

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setProxy(proxy);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        String body = "{\"email\":\"" + email + "\",\"number\":\"" + authNumber + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        String response = restTemplate.postForObject("http://43.202.141.142:8080/pusanuniv", httpEntity, String.class);
        System.out.println(response);
    }

    public void checkPNUEmail(MemberRequest.checkPNUEmailRequestDTO pnuEmailRequestDTO) {
        Member member;
        try {
            member = findMember();
        } catch (Exception404 e) {
            log.error("Access Token에서 뽑아낸 회원이 존재하지 않는 회원입니다. (부산대 인증 메일 확인)");
            throw e;
        }
        if(!redisUtility.getValues(pnuEmailRequestDTO.getEmail()).equals(pnuEmailRequestDTO.getCertificationNumber())){
            log.error("부산대 메일 인증 번호가 틀렸습니다. User Id : " + member.getEmail());
            throw new Exception400("인증번호가 틀렸습니다.");
        }

        redisUtils.setGroupIdValues(MEMBER_ID_PREFIX + member.getId(), PNU_GROUP_ID, Duration.ofHours(MEMBER_ID_LIFETIME));
    }

    public void findPassword(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if(member.isEmpty()) {
            log.error("가입된 회원이 존재하지 않는 이메일입니다. (비밀번호 찾기)");
            throw new Exception404("없는 회원입니다.");
        }
        String randomPassword = makeRandomPassword();
        member.get().changePassword(passwordEncoder.encode(randomPassword));

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setProxy(proxy);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + randomPassword + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        String response = restTemplate.postForObject("http://43.202.141.142:8080/password/find", httpEntity, String.class);
        System.out.println(response);
    }

    public Integer makeEmailAuthNum() {
        return new Random().nextInt(888888) + 111111;
    }

    public String makeRandomPassword() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit,rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

    }

    private Member findMember() {
        Optional<Member> member = memberRepository.findById(currentMember());
        if(member.isEmpty()) {
            throw new Exception404("없는 회원입니다.");
        }
        return member.get();
    }

    public MemberResponse.authTokenDTO getKakaoInfo(String code) {
        String accessToken = "";
        log.info("카카오 엑세스 토큰 발급 시작");
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", KAKAO_CLIENT_ID);
            params.add("code", code);
            params.add("redirect_uri", KAKAO_REDIRECT_URI);

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setProxy(proxy);

            RestTemplate restTemplate = new RestTemplate(requestFactory);
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );
            log.info("카카오 엑세스 토큰 발급 완료");

            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            MemberResponse.KakaoTokenDTO kakaoTokenDTO = objectMapper.readValue(response.getBody(), MemberResponse.KakaoTokenDTO.class);
            accessToken = kakaoTokenDTO.getAccess_token();
        }  catch (JsonProcessingException e) {
            log.error("파싱 오류", e);
            throw new Exception500("Json 파싱 에러입니다.");
        }

        MemberResponse.KakaoInfoDTO kakaoInfo = getUserInfoWithToken(accessToken);
        String kakaoEmail = kakaoInfo.getId() + "@wekiki.com";
        Optional<Member> kakaoMember = memberRepository.findByEmail(kakaoEmail);
        if(kakaoMember.isEmpty()){
            log.info("없는 회원이니 카카오 회원가입을 진행합니다.");
            kakaoSignUp(kakaoInfo, kakaoEmail);
        }
        MemberRequest.loginRequestDTO kakaoLogin = new MemberRequest.loginRequestDTO(kakaoEmail,KAKAO_PASSWORD);
        return login(kakaoLogin);
    }

    private void kakaoSignUp(MemberResponse.KakaoInfoDTO kakaoInfo, String kakaoEmail) {
        Member member = Member.builder()
                .name(kakaoInfo.getProperties().getNickname())
                .email(kakaoEmail)
                .password(passwordEncoder.encode(KAKAO_PASSWORD))
                .created_at(LocalDateTime.now())
                .authority(Authority.user)
                .build();
        memberRepository.save(member);
    }

    private MemberResponse.KakaoInfoDTO getUserInfoWithToken(String accessToken) {
        MemberResponse.KakaoInfoDTO kakaoInfo = null;

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setProxy(proxy);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            kakaoInfo = objectMapper.readValue(response.getBody(),MemberResponse.KakaoInfoDTO.class);

        } catch (JsonProcessingException e) {
            throw new Exception500("Json 파싱 에러입니다." + e);
        }
        return kakaoInfo;
    }
}
