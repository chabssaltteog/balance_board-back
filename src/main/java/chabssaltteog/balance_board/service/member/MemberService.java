package chabssaltteog.balance_board.service.member;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.VoteMember;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.member.LoginResponseDTO;
import chabssaltteog.balance_board.dto.member.ProfilePostDTO;
import chabssaltteog.balance_board.dto.member.ProfilePostResponseDTO;
import chabssaltteog.balance_board.dto.member.ProfileInfoResponseDTO;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.repository.PostRepository;
import chabssaltteog.balance_board.repository.VoteMemberRepository;
import chabssaltteog.balance_board.util.JwtToken;
import chabssaltteog.balance_board.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public ProfileInfoResponseDTO getProfile(Long userId) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 사용자입니다."));

        //todo 토큰으로 사용자 정보 뽑아내고, 이 사용자 정보랑 userId랑 같은지 확인
//
//        List<Post> myPosts = member.getPosts();
//        List<ProfilePostDTO> userPosts = myPosts.stream()
//                .map(ProfilePostDTO::toDTO).toList();   // 작성한 글들
//
//        List<VoteMember> voteMembers = voteMemberRepository.findByUser(member);
//
//        List<ProfilePostDTO> votedPosts = voteMembers.stream()
//                .map(voteMember -> ProfilePostDTO.toDTO(voteMember.getVote().getPost()))
//                .collect(Collectors.toList());  // 투표한 글들

        return ProfileInfoResponseDTO.builder()
                .userId(userId)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .imageType(member.getImageType())
                .build();
    }

    /**
     * @param listType 1 -> 전체
     *                 2 -> 작성한 글
     *                 3 -> 투표한 글
     */

    //todo 페이징 방법 수정
    public ProfilePostResponseDTO getProfilePosts(Long userId, int listType, int page) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 사용자입니다."));

        List<ProfilePostDTO> profilePosts = new ArrayList<>();

        if (listType == 1) {
            List<VoteMember> voteMembers = voteMemberRepository.findByUser(member);
            List<Post> writedPosts = member.getPosts();
            List<Post> votedPosts = voteMembers.stream().map(voteMember -> voteMember.getVote().getPost()).toList();

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
        } else if (listType == 2) {
            profilePosts = member.getPosts().stream().map(post -> {
                return ProfilePostDTO.toDTO(post, true, false);
            }).toList();
        } else if (listType == 3) {
            List<VoteMember> voteMembers = voteMemberRepository.findByUser(member);
            List<Post> votedPosts = voteMembers.stream().map(voteMember -> voteMember.getVote().getPost()).toList();
            profilePosts = votedPosts.stream().map(post -> {
                return ProfilePostDTO.toDTO(post, false, true);
            }).toList();
        }

        List<ProfilePostDTO> sortedPosts = profilePosts.stream()
                .sorted(Comparator.comparing(ProfilePostDTO::getCreated).reversed())
                .collect(Collectors.toList());

        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) sortedPosts.size() / pageSize); //총 페이지 개수 계산

        if (page < 1 || page > totalPages) { // 페이지 번호가 유효하지 않은 경우 빈 배열 반환
            return new ProfilePostResponseDTO(0, Collections.emptyList());
        }

        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, sortedPosts.size());
        List<ProfilePostDTO> pagePosts = sortedPosts.subList(fromIndex, toIndex);

        return new ProfilePostResponseDTO(profilePosts.size(), pagePosts);
    }



}
