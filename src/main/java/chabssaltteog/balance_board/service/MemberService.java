package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public long updateMemberInfo(String email, String nickname, int birthYear, String gender) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 회원 정보(닉네임, 나이, 성별) 추가
        member.updateNickNameBirthYearGender(nickname, birthYear, gender);
        return member.getUserId();
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> nickname = memberRepository.findByNickname(member.getNickname());
        if (!nickname.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }

//        int currentYear = Year.now().getValue();
//        if (member.getBirthYear() < 1900 || member.getBirthYear() > currentYear) {
//            throw new IllegalStateException("유효하지 않은 출생년도입니다.");
//        }
    }

    // 추가 정보 확인
    public boolean additionalInfoRequired(String email) {

        Optional<Member> optionalMember = findMember(email);

        return optionalMember.map(member ->
                member.getNickname() == null || member.getBirthYear() == 0 || member.getGender() == null
        ).orElse(false);
    }

    public Optional<Member> findMember(String email) {

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember;
    }
}
