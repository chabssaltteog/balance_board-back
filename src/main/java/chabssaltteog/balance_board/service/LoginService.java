package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


//    /**
//     * @param email
//     * @param rawPassword
//     * @return null 로그인 실패
//     */
//    public Member login(String email, String rawPassword) {
//        return memberRepository.findByEmail(email)
//                .filter(m -> passwordEncoder.matches(rawPassword, m.getPassword()))
//                .orElse(null);
//    }
}
