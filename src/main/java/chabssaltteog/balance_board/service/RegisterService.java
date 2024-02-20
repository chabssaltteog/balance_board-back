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

import static chabssaltteog.balance_board.api.MemberController.*;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(CreateMemberRequestDTO requestDTO) {

        //email 중복 체크
        if (memberRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 email입니다."); //todo
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
        Member member = new Member(requestDTO.getEmail(), encodedPassword,
                requestDTO.getNickname(), requestDTO.getBirthYear(), requestDTO.getGender());
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        member.setRoles(roles);

        memberRepository.save(member);
    }
}
