package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.api.MemberController;
import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static chabssaltteog.balance_board.api.MemberController.*;

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

        return CreateMemberResponse.toDto(memberRepository.save(requestDTO.toEntity(encodedPassword, roles)));
    }

    public boolean validateDuplicateEmail(String email) {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        return byEmail.isPresent();
    }
}
