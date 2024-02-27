package chabssaltteog.balance_board.service.member;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public boolean validateDuplicateEmail(String email) {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        return byEmail.isPresent();
    }
}
