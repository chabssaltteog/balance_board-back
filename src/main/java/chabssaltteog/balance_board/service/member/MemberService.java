package chabssaltteog.balance_board.service.member;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.VoteMember;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.LoginResponseDTO;
import chabssaltteog.balance_board.dto.ProfilePostDTO;
import chabssaltteog.balance_board.dto.ProfileResponseDTO;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.repository.VoteMemberRepository;
import chabssaltteog.balance_board.util.JwtToken;
import chabssaltteog.balance_board.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final VoteMemberRepository voteMemberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JwtToken signIn(String email, String password) {

        memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다."));

        // 1. email + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        log.info("authenticationToken = {}", authenticationToken);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByEmail 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("authentication = {}", authentication);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        log.info("jwtToken = {}", jwtToken);

        return jwtToken;
    }

    public boolean validateDuplicateNickname(String nickname) {
        Optional<Member> byNickname = memberRepository.findByNickname(nickname);
        return byNickname.isPresent();
    }

    public LoginResponseDTO getUserInfoAndGenerateToken(String token) {

        // 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        String email = authentication.getName();

        // 사용자 정보 가져오기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다."));

        JwtToken newToken = jwtTokenProvider.generateToken(authentication);
        log.info("newToken = {}", newToken);

        return new LoginResponseDTO(
                member.getEmail(),
                newToken,
                member.getUserId(),
                member.getImageType(),
                member.getNickname()
        );
    }

    public ProfileResponseDTO getProfile(Long userId) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 사용자입니다."));

        List<Post> myPosts = member.getPosts();
        List<ProfilePostDTO> userPosts = myPosts.stream()
                .map(ProfilePostDTO::toDTO).toList();   // 작성한 글들

        List<VoteMember> voteMembers = voteMemberRepository.findByUser(member);

        List<ProfilePostDTO> votedPosts = voteMembers.stream()
                .map(voteMember -> ProfilePostDTO.toDTO(voteMember.getVote().getPost()))
                .collect(Collectors.toList());  // 투표한 글들

        return ProfileResponseDTO.builder()
                .userId(userId)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .imageType(member.getImageType())
                .userPosts(userPosts)
                .votedPosts(votedPosts)
                .build();
    }


}
