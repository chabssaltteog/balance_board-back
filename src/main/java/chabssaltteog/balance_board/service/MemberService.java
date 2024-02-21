package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.util.JwtToken;
import chabssaltteog.balance_board.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JwtToken signIn(String email, String password) {

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("가입되지 않은 계정입니다.");
        }

        // 1. email + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        log.info("authenticationToken = {}", authenticationToken);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByEmail 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("authentication = {}", authentication);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        log.info("jwtToken = {}", jwtToken);

        return jwtToken;
    }

    public boolean validateDuplicateNickname(String nickname) {
        Optional<Member> byNickname = memberRepository.findByNickname(nickname);
        return byNickname.isPresent();
    }

//    @Transactional
//    public long updateMemberInfo(String email, String nickname, int birthYear, String gender) {
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
//
//        // 회원 정보(닉네임, 나이, 성별) 추가
//        member.updateNickNameBirthYearGender(nickname, birthYear, gender);
//        return member.getUserId();
//    }


//
//    // 추가 정보 확인
//    public boolean confirmRequiredInfo(String email) {
//
//        Optional<Member> optionalMember = findMember(email);
//
//        return optionalMember.map(member ->
//                member.getNickname() == null || member.getBirthYear() == 0 || member.getGender() == null
//        ).orElse(false);
//    }
//
//    public Optional<Member> findMember(String email) {
//
//        Optional<Member> optionalMember = memberRepository.findByEmail(email);
//        return optionalMember;
//    }
}
