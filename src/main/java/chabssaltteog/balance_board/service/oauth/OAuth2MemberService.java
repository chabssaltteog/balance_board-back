package chabssaltteog.balance_board.service.oauth;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.domain.oauth.KakaoMemberInfo;
import chabssaltteog.balance_board.domain.oauth.Oauth2MemberInfo;
import chabssaltteog.balance_board.domain.oauth.PrincipalDetails;
import chabssaltteog.balance_board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final LoginService loginService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("-- loadUser 실행 --");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Oauth2MemberInfo memberInfo = null;

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registrationId = {}", registrationId);
        if (registrationId.equals("kakao")) {
            memberInfo = new KakaoMemberInfo(oAuth2User.getAttributes());
        } else throw new OAuth2AuthenticationException("400");
        log.info("memberInfo.getEmail = {}", memberInfo.getEmail());
        log.info("memberInfo.getNickname = {}", memberInfo.getName());

        String provider = memberInfo.getProvider();
        String providerId = memberInfo.getProviderId();
//        String username = provider + "_" + providerId;
        String email = memberInfo.getEmail();
//        String nickname = memberInfo.getName();

        Member userEntity = null;
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        userEntity = optionalMember.orElseGet(() -> loginService.registerOauth(provider, providerId, email));
        log.info("userEntity.getEmail = {}", userEntity.getEmail());

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
