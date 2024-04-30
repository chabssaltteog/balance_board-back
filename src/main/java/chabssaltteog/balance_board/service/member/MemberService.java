package chabssaltteog.balance_board.service.member;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.domain.vote.VoteMember;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.member.ProfilePostDTO;
import chabssaltteog.balance_board.dto.member.ProfilePostResponseDTO;
import chabssaltteog.balance_board.dto.member.ProfileInfoResponseDTO;
import chabssaltteog.balance_board.exception.InvalidUserException;
import chabssaltteog.balance_board.dto.member.*;
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

import java.util.*;
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
    private final RefreshTokenService refreshTokenService;


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

    public LoginTokenResponseDTO getUserInfoAndGenerateToken(String accessToken, String refreshToken) {

        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String email = authentication.getName();
        log.info("token login : access token - user email = {}", email);

        // 사용자 정보 가져오기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다."));

        //access token의 userId와 refresh token의 userId가 같으면 userId 반환
        Long userId = refreshTokenService.matches(refreshToken, email);
        log.info("refresh token userId == access token userId");

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        log.info("newAccessToken = {}", newAccessToken);

        return LoginTokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .level(member.getLevel().getValue())
                .experiencePoints(member.getExperiencePoints())
//                .imageType(member.getImageType())
                .userId(userId)
                .build();
    }

    public ProfileInfoResponseDTO getProfile(Long userId) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 사용자입니다."));

        //todo 토큰으로 사용자 정보 뽑아내고, 이 사용자 정보랑 userId랑 같은지 확인

        return ProfileInfoResponseDTO.builder()
                .userId(userId)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .level(member.getLevel())
                .experiencePoints(member.getExperiencePoints())
//                .imageType(member.getImageType())
                .build();
    }

    /**
     * @param listType 0 -> 전체
     *                 1 -> 작성한 글
     *                 2 -> 투표한 글
     */
    //todo 페이징 변경, 코드 전체 리펙토링 -> 필터링
    public ProfilePostResponseDTO getProfilePosts(Long userId, int listType, int page) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 사용자입니다."));

        List<ProfilePostDTO> profilePosts = new ArrayList<>();
        List<Post> writedPosts = new ArrayList<>();
        List<Post> votedPosts = new ArrayList<>();

        if (listType == 0) {
            List<VoteMember> voteMembers = voteMemberRepository.findByUser(member);
            writedPosts = member.getPosts();
            votedPosts = voteMembers.stream().map(voteMember -> voteMember.getVote().getPost()).toList();

            Set<Post> uniquePosts = new HashSet<>(writedPosts);
            uniquePosts.addAll(votedPosts);

            for (Post post : uniquePosts) {
                boolean isWrited = writedPosts.contains(post);
                boolean isVoted = votedPosts.contains(post);

                // 작성한 글이면서 투표한 글인 경우
                if (isWrited && isVoted) {
                    profilePosts.add(ProfilePostDTO.toDTO(post, true, true));
                } else {
                    profilePosts.add(ProfilePostDTO.toDTO(post, isWrited, isVoted));
                }
            }
        }
        else if (listType == 1) {
            writedPosts = member.getPosts();
            profilePosts = writedPosts.stream().map(post -> {
                return ProfilePostDTO.toDTO(post, true, false);
            }).toList();
        }
        else if (listType == 2) {
            List<VoteMember> voteMembers = voteMemberRepository.findByUser(member);
            votedPosts = voteMembers.stream().map(voteMember -> voteMember.getVote().getPost()).toList();
            profilePosts = votedPosts.stream().map(post -> {
                return ProfilePostDTO.toDTO(post, false, true);
            }).toList();
        }

        List<ProfilePostDTO> sortedPosts = profilePosts.stream()
                .sorted(Comparator.comparing(ProfilePostDTO::getCreated).reversed())
                .collect(Collectors.toList());

        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) sortedPosts.size() / pageSize);

        if (page > totalPages) {
            return new ProfilePostResponseDTO(0, 0, 0, Collections.emptyList());
        }

        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, sortedPosts.size());
        List<ProfilePostDTO> pagePosts = sortedPosts.subList(fromIndex, toIndex);

        return new ProfilePostResponseDTO(profilePosts.size(), writedPosts.size(), votedPosts.size(), pagePosts);
    }


    @Transactional
    public String changeNickname(NicknameRequestDTO requestDTO, Authentication authentication) {
        String newNickname = requestDTO.getNickname();
        Long userId = requestDTO.getUserId();
        Member member = getMember(authentication);

        if (!member.getUserId().equals(userId)) {
            throw new InvalidUserException("올바른 사용자의 요청이 아닙니다.");
        }
        member.setNickname(newNickname);
        return newNickname;
    }

    public Member getMember(Authentication authentication) {
        String email = authentication.getName();
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다.");
        }
        return optionalMember.get();
    }

}
