package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void updateMemberInfo(String email, String nickname, int age, String gender) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 회원 정보(닉네임, 나이, 성별) 추가
        member.updateNickNameAgeGender(nickname, age, gender);
        memberRepository.save(member);
    }
}
