package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.domain.vote.VoteMember;
import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Comment;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.domain.post.Tag;
import chabssaltteog.balance_board.dto.post.*;
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
                    String selectedOption = getSelectedOption(post, member);
                    return PostDTO.toDTO(post, selectedOption);
                })
                .toList();
    }

    // 상세 게시글
    public PostDTO getPostByPostId(Long postId, Authentication authentication) {

        if (authentication == null) {
            log.info("==비로그인 상세 페이지 조회==");
            Post post = postService.getPostByPostId(postId);
            return PostDTO.toDetailDTO(post);
        }

        Member member = getMember(authentication);

        Long userId = member.getUserId();
        log.info("상세 게시글 조회 userId = {}", userId);

        Post post = postService.getPostByPostId(postId);

        String selectedOption = getSelectedOption(post, member);
        return PostDTO.toDetailDTO(post, selectedOption);
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
                    String selectedOption = getSelectedOption(post, member);
                    return PostDTO.toDTO(post, selectedOption);
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
    public CommentDTO addCommentToPost(CreateCommentRequestDTO requestDTO) {

        return postService.addCommentToPost(requestDTO);
    }

    private String getSelectedOption(Post post, Member member) {
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

}
