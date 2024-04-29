package chabssaltteog.balance_board.service.member;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.domain.oauth.PrincipalDetails;
import chabssaltteog.balance_board.exception.InvalidUserException;
import chabssaltteog.balance_board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static chabssaltteog.balance_board.api.member.MemberController.*;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CreateMemberResponse register(CreateMemberRequestDTO requestDTO) {

//        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
//        List<String> roles = new ArrayList<>();
//        roles.add("USER");
//        Member member = requestDTO.toEntity(encodedPassword, roles);

//        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
//        Member member = principal.getMember();
//        int randomNum = new Random().nextInt(5) + 1;

        Member member = memberRepository.findByUserId(requestDTO.getUserId());
        if (member == null) {
            throw new NoSuchElementException("userID 가 잘못되었습니다.");
        }

        if (member.getNickname() == null | member.getGender() == null | member.getBirthYear() == null) {
            member.addInfo(requestDTO.getNickname(), requestDTO.getBirthYear(), requestDTO.getGender());
        } else {
            throw new InvalidUserException("기존 사용자입니다.");
        }

        //authentication principal member 변경
//        updatePrincipalDetailsWithAdditionalInfo(member);

        return CreateMemberResponse.toDto(memberRepository.save(member));
    }

    public boolean validateDuplicateEmail(String email) {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        return byEmail.isPresent();
    }

    /**
    @Transactional
    public void updatePrincipalDetailsWithAdditionalInfo(Member member) {
        PrincipalDetails updatedPrincipalDetails = new PrincipalDetails(member, null); // attributes는 null로 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(updatedPrincipalDetails, null, updatedPrincipalDetails.getAuthorities())
        );
    } */
}
