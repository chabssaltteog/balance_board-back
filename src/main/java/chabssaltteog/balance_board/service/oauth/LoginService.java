package chabssaltteog.balance_board.service.oauth;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.domain.oauth.api.KakaoProfile;
import chabssaltteog.balance_board.domain.oauth.api.OauthToken;
import chabssaltteog.balance_board.dto.oauth.KakaoLoginResponseDTO;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.util.JwtToken;
import chabssaltteog.balance_board.util.JwtTokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String client_id;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String client_secret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirect_uri;

    public Member registerOauth(String provider, String providerId, String email) {
        return Member.builder()
                .email(email)
                .role("ROLE_USER")
                .provider(provider)
                .providerId(providerId)
                .build();
    }

    public OauthToken getAccessToken(String code) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client_id);
        params.add("redirect_uri", redirect_uri);
        params.add("code", code);
        params.add("client_secret", client_secret);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OauthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            log.info("getAccessToken 실행");
            log.info("JsonProcessingException = {}", e.getMessage());
            e.printStackTrace();
        }

        return oauthToken;
    }

    public KakaoLoginResponseDTO saveKakaoMem(String token) {
        KakaoProfile profile = findProfile(token);
        if (profile == null) {
            return null;
        }
        Optional<Member> optionalMember = memberRepository.findByEmail(profile.kakao_account.getEmail());
        if (optionalMember.isEmpty()) {
            log.info("신규 사용자");
            Member member = Member.builder()
                    .email(profile.kakao_account.getEmail())
                    .role("ROLE_USER")
                    .provider("kakao")
                    .providerId(profile.getId().toString())
//                    .imageType(new Random().nextInt(5) + 1)
                    .build();
            memberRepository.save(member);

            JwtToken jwtToken = generateJwtToken(member);

            return new KakaoLoginResponseDTO(
                    member.getUserId(), 0, member.getEmail(), null,
                    member.getLevel().getValue(), member.getExperiencePoints(), jwtToken);
        } else {
            Member member = optionalMember.get();
            log.info("기존 사용자");

            JwtToken jwtToken = generateJwtToken(member);

            return new KakaoLoginResponseDTO(
                    member.getUserId(), 1, member.getEmail(), member.getNickname(),
                    member.getLevel().getValue(), member.getExperiencePoints(), jwtToken);
        }
    }

    private JwtToken generateJwtToken(Member member) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }

    public KakaoProfile findProfile(String token) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers);

        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String> kakaoProfileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            log.info("findProfile 실행");
            log.info("JsonProcessingException = {}", e.getMessage());
            e.printStackTrace();
        }

        return kakaoProfile;
    }

}
