package chabssaltteog.balance_board.service.member;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static chabssaltteog.balance_board.api.member.MemberController.*;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /*@Transactional
    public CreateMemberResponse register(CreateMemberRequestDTO requestDTO) {

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        List<String> roles = new ArrayList<>();
        roles.add("USER");
        Member member = requestDTO.toEntity(encodedPassword, roles);
        int randomNum = new Random().nextInt(5) + 1;
        member.setImageType(randomNum);

        return CreateMemberResponse.toDto(memberRepository.save(member));
    }*/

    @Transactional
    public CreateMemberResponse register(CreateMemberRequestDTO requestDTO) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        // 동일한 이메일의 회원 레코드가 이미 존재하는지 확인
        Optional<Member> optionalExistingMember = memberRepository.findByEmail(requestDTO.getEmail());
        if (optionalExistingMember.isPresent()) {
            // 기존 회원이 존재할 경우 해당 회원 레코드 업데이트
            Member existingMember = optionalExistingMember.get();
            existingMember.updateWithNewData(requestDTO, encodedPassword); // 회원 정보 업데이트 메서드 호출
            Member updatedMember = memberRepository.save(existingMember); // 회원 정보 저장 및 업데이트된 회원 반환
            return CreateMemberResponse.toDto(updatedMember); // 업데이트된 회원 정보를 응답 DTO로 변환하여 반환
        } else {
            // 기존 회원이 존재하지 않을 경우 새로운 회원 생성
            Member newMember = requestDTO.toEntity(encodedPassword, Collections.singletonList("USER"));
            int randomNum = new Random().nextInt(5) + 1;
            newMember.setImageType(randomNum);
            Member savedMember = memberRepository.save(newMember); // 새 회원 정보 저장
            return CreateMemberResponse.toDto(savedMember); // 새 회원 정보를 응답 DTO로 변환하여 반환
        }
    }

    public int validateDuplicateEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) { // 회원조회
            Member existingMember = optionalMember.get(); // 회원정보 저장
            Boolean withdrawn = existingMember.getWithdrawn();
            if (withdrawn != null && withdrawn) { // 탈퇴한 경우
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime thirtyDaysAgo = now.minusDays(30);
                if (existingMember.getWithdrawnDate() != null && existingMember.getWithdrawnDate().isAfter(thirtyDaysAgo)) {// 30일 이내 탈퇴
                    return 3; // 30일 이내 재가입 제한
                } else {
                    return 4; // 30일 경과 유저
                }
            } else if (withdrawn == null) { // 탈퇴하지 않은 경우
                return 2; // 이메일 중복
            }
        }
        return 1; // 신규 회원가입 가능
    }
}
