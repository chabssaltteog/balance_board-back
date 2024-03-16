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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static chabssaltteog.balance_board.api.member.MemberController.*;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CreateMemberResponse register(CreateMemberRequestDTO requestDTO) {

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        Member member = requestDTO.toEntity(encodedPassword, roles);
        int randomNum = new Random().nextInt(5) + 1;
        member.setImageType(randomNum);

        return CreateMemberResponse.toDto(memberRepository.save(member));
    }

    /*public boolean validateDuplicateEmail(String email) {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        return byEmail.isPresent();
    }*/

    /*public int validateDuplicateEmail(String email){
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if(optionalMember.isPresent()){ //회원조회
            Member existingMember = optionalMember.get(); //회원정보 저장
            if(existingMember.isWithdrawn()){ //탈퇴한 경우
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime thirtyDaysAgo = now.minusDays(30);
                if(existingMember.getWithdrawnDate() != null && existingMember.getWithdrawnDate().isAfter(thirtyDaysAgo)){//30일 이내 탈퇴
                    return 3; //30일 이내 재가입 제한
                }else{
                    return 4; //30일 경과 유저
                }
            }else{
                return 2; //이메일 중복
            }

        }else {
            return 1; //신규 회원가입 가능
        }

    }*/

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
