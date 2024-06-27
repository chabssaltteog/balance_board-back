package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.domain.post.Like;
import chabssaltteog.balance_board.domain.vote.VoteMember;
import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.domain.post.Tag;
import chabssaltteog.balance_board.dto.post.*;
import chabssaltteog.balance_board.repository.LikeRepository;
import chabssaltteog.balance_board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MainService {

    private final PostService postService;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;

    // 메인 페이지
    public List<PostDTO> getAllPosts(int page, Authentication authentication) {

        if (authentication == null) {
            log.info("==비로그인 메인 페이지 조회==");
            Page<Post> posts = postService.getAllPosts(page);

            return posts.getContent()
                    .stream()
                    .map(PostDTO::toDTO)
                    .toList();
        }

        Member member = getMember(authentication);
        Long userId = member.getUserId();
        log.info("모든 게시글 조회 userId = {}", userId);

        Page<Post> posts = postService.getAllPosts(page);

        return posts.getContent()
                .stream()
                .map(post -> {
                    String selectedVoteOption = getSelectedVoteOption(post, member);
                    String selectedLikeOption = getSelectedLikeOption(post.getPostId(), userId);
                    return PostDTO.toDTO(post, selectedVoteOption, selectedLikeOption);
                })
                .toList();
    }

    //인기 게시글 top 3
    public List<PostDTO> getHotPosts(Authentication authentication) {

        if (authentication == null) {
            log.info("==비로그인 메인 페이지 인기글 조회==");
            List<Post> hotPosts = postService.getHotPosts();

            return hotPosts.stream()
                    .map(PostDTO::toDTO)
                    .toList();
        }
        Member member = getMember(authentication);
        Long userId = member.getUserId();
        log.info("인기 게시글 조회 userId = {}", userId);

        List<Post> hotPosts = postService.getHotPosts();

        return hotPosts.stream()
                .map(post -> {
                    String selectedVoteOption = getSelectedVoteOption(post, member);
                    String selectedLikeOption = getSelectedLikeOption(post.getPostId(), userId);
                    return PostDTO.toDTO(post, selectedVoteOption, selectedLikeOption);
                })
                .toList();
    }

    // 상세 게시글
    public PostDetailDTO getPostByPostId(Long postId, Authentication authentication) {

        if (authentication == null) {
            log.info("==비로그인 상세 페이지 조회==");
            Post post = postService.getPostByPostId(postId);
            return PostDetailDTO.toDetailDTO(post);
        }

        Member member = getMember(authentication);

        Long userId = member.getUserId();
        log.info("상세 게시글 조회 userId = {}", userId);

        Post post = postService.getPostByPostId(postId);

        String selectedVoteOption = getSelectedVoteOption(post, member);
        String selectedLikeOption = getSelectedLikeOption(post.getPostId(), member.getUserId());
        return PostDetailDTO.toDetailDTO(post, selectedVoteOption, selectedLikeOption);
    }

    // 게시글 카테고리 필터링
    public List<PostDTO> getPostsByCategory(Category category, int page, Authentication authentication) {

        if (authentication == null) {
            log.info("==비로그인 카테고리 필터링 조회==");
            List<Post> posts = postService.getPostsByCategory(category, page);
            return posts.stream()
                    .map(PostDTO::toDTO)
                    .toList();
        }

        log.info("CATEGORY SEARCH : category = {}", category);
        log.info("==카테고리 필터링 조회==");
        Member member = getMember(authentication);
        List<Post> posts = postService.getPostsByCategory(category, page);

        return posts.stream()
                .map(post -> {
                    String selectedVoteOption = getSelectedVoteOption(post, member);
                    String selectedLikeOption = getSelectedLikeOption(post.getPostId(), member.getUserId());
                    return PostDTO.toDTO(post, selectedVoteOption, selectedLikeOption);
                })
                .toList();
    }

    // 게시글 작성
    @Transactional
    public CreatePostResponseDTO createPost(CreatePostRequestDTO requestDTO) {

        Category category = Category.valueOf(requestDTO.getCategory());
        log.info("CREATE POST Category = {}", category);

        Long userId = requestDTO.getUserId();
        Member member = memberRepository.findByUserId(userId);
        if(member == null) {
            throw new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다.");
        }

        Post post = Post.builder()
                .title(requestDTO.getTitle())
                .category(category)
                .content(requestDTO.getContent())
                .build();

        // 태그들 저장
        for (String tagName : requestDTO.getTags()) {
            Tag tag = Tag.builder().tagName(tagName).build();
            post.addTag(tag);
        }
        log.info("CREATE POST Tags = {}", requestDTO.getTags());

        if (requestDTO.getOption1().equals(requestDTO.getOption2())) {
            throw new RuntimeException("투표의 투표 옵션들이 같습니다.");
        }
        post.setVoteOptions(requestDTO.getOption1(), requestDTO.getOption2());

        Post createdPost = postService.createPost(userId, post);

        int preLevel = member.getLevel().getValue();

        int experiencePoints = member.incrementExperiencePoints(3); // 글 작성
        int updatedLevel = member.updateLevel(experiencePoints);

        // Level Up
        if (preLevel != updatedLevel) {
            return CreatePostResponseDTO.builder()
                    .postId(createdPost.getPostId())
                    .created(createdPost.getCreated())
                    .userId(userId)
                    .isLevelUp(true)
                    .updatedLevel(updatedLevel)
                    .build();
        } else {
            return CreatePostResponseDTO.builder()
                    .postId(createdPost.getPostId())
                    .created(createdPost.getCreated())
                    .userId(userId)
                    .isLevelUp(false)
                    .updatedLevel(updatedLevel)
                    .build();
        }
    }

    // 게시글에 댓글 달기
    @Transactional
    public CreateCommentResponseDTO addCommentToPost(CreateCommentRequestDTO requestDTO) {

        return postService.addCommentToPost(requestDTO);
    }

    //게시글에 좋아요/싫어요 누르기
    @Transactional
    public LikeHateResponseDTO likeOrHatePost(LikeHateRequestDTO likeHateDTO) throws Exception {
        return postService.likeOrHatePost(likeHateDTO);
    }

    private String getSelectedVoteOption(Post post, Member member) {
        Optional<VoteMember> voteMember = member.getVoteMembers()
                .stream()
                .filter(vm -> vm.getVote().getPost().equals(post))
                .findFirst();
        log.info("===voteMember=== {}", voteMember);

        return voteMember.map(VoteMember::getVotedOption).orElse(null);
    }

    public Member getMember(Authentication authentication) {
        String email = authentication.getName();
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다.");
        }
        return optionalMember.get();
    }

    private String getSelectedLikeOption(Long postId, Long userId) {
        Optional<Like> optionalLike = likeRepository.findByUser_UserIdAndPost_PostId(userId, postId);
        if (optionalLike.isEmpty()) {
            return null;
        }
        Like like = optionalLike.get();
        boolean isLike = like.isLike();
        if (isLike) {
            return "like";
        } else return "hate";
    }
}
