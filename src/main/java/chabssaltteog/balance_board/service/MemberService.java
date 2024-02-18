package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public boolean validateDuplicateMember(String nickname) {
        Optional<Member> byNickname = memberRepository.findByNickname(nickname);
        return byNickname.isPresent();
    }

    // 추가 정보 확인
    public boolean confirmRequiredInfo(String email) {

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
