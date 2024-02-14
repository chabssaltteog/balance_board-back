package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {    //spring security
        OAuth2User oAuth2User = super.loadUser(userRequest);
        saveGoogleUser(oAuth2User);
        return oAuth2User;
    }

    private void saveGoogleUser(OAuth2User oAuth2User) {
        Long googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<Member> member = memberRepository.findByProviderId(googleId);
        if (member.isEmpty()) {
            member = Optional.of(new Member(googleId, email, name));
            // 추가적인 사용자 정보는 폼 데이터로 받아서 설정

        } else {
            // 이미 해당 구글 ID로 등록된 사용자가 있다면 업데이트


        }
        memberRepository.save(member);
    }
}
