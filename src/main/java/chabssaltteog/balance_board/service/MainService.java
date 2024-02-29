package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.VoteMember;
import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Comment;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.domain.post.Tag;
import chabssaltteog.balance_board.dto.*;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MainService {

    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;


    // 메인 페이지 -> 비로그인
    public List<PostDTO> getAllPosts(int pageNumber, int pageSize) {
        List<Post> posts = postService.getAllPosts();

        int totalPages = (int) Math.ceil((double) posts.size() / pageSize); //총 페이지 개수 계산

        if (pageNumber < 1 || pageNumber > totalPages) { // 페이지 번호가 유효하지 않은 경우 빈 배열 반환
            return Collections.emptyList();
        }

        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, posts.size());

        return posts.subList(fromIndex, toIndex)
                .stream()
                .map(PostDTO::toDTO)
                .toList();
    }

    // 메인 페이지 -> 로그인
    public List<PostDTO> getAllPosts(int pageNumber, int pageSize, String token) {

        Member member = getMember(token);
        Long userId = member.getUserId();
        log.info("모든 게시글 조회 userId = {}", userId);

        List<Post> posts = postService.getAllPosts();

        int totalPages = (int) Math.ceil((double) posts.size() / pageSize); //총 페이지 개수 계산

        if (pageNumber < 1 || pageNumber > totalPages) { // 페이지 번호가 유효하지 않은 경우 빈 배열 반환
            return Collections.emptyList();
        }

        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, posts.size());

        return posts.subList(fromIndex, toIndex)
                .stream()
                .map(post -> {
                    String selectedOption = getSelectedOption(post, member);
                    return PostDTO.toDTO(post, selectedOption);
                })
                .toList();
    }

    // 상세 게시글 -> 비로그인

    public PostDTO getPostByPostId(Long postId) {

        Post post = postService.getPostByPostId(postId);
        return PostDTO.toDetailDTO(post);
    }
    // 상세 게시글 -> 로그인

    public PostDTO getPostByPostId(Long postId, String token) {

        Member member = getMember(token);
        Long userId = member.getUserId();
        log.info("상세 게시글 조회 userId = {}", userId);

        Post post = postService.getPostByPostId(postId);

        String selectedOption = getSelectedOption(post, member);
        return PostDTO.toDetailDTO(post, selectedOption);
    }
    private String getSelectedOption(Post post, Member member) {
        Optional<VoteMember> voteMember = member.getVoteMembers()
                .stream()
                .filter(vm -> vm.getVote().getPost().equals(post))
                .findFirst();
        log.info("===voteMember=== {}", voteMember);

        return voteMember.map(VoteMember::getVotedOption).orElse(null);
    }

    private Member getMember(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        String email = authentication.getName();

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다.");
        }
        Member member = optionalMember.get();


        return member;
    }


    // 게시글 카테고리 필터링

    public List<PostDTO> getPostsByCategory(Category category, int pageNumber, int pageSize) {
        log.info("카테고리 필터링 조회 category = {}", category);
        List<Post> posts = postService.getPostsByCategory(category);
        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, posts.size());

        return posts.subList(fromIndex, toIndex)
                .stream()
                .map(PostDTO::toDTO)
                .toList();
    }

    // 게시글 작성
    @Transactional
    public CreatePostResponseDTO createPost(CreatePostRequestDTO requestDTO) {

        Category category = Category.valueOf(requestDTO.getCategory());
        log.info("CREATE POST Category = {}", category);

        Long userId = requestDTO.getUserId();

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

        post.setVoteOptions(requestDTO.getOption1(), requestDTO.getOption2());

        Post createdPost = postService.createPost(userId, post);

        return CreatePostResponseDTO.builder()
                .postId(createdPost.getPostId())
                .created(createdPost.getCreated())
                .userId(userId)
                .build();
    }

    // 게시글에 댓글 달기
    @Transactional
    public CommentDTO addCommentToPost(CreateCommentRequestDTO requestDTO) {

        Comment addedComment = postService.addCommentToPost(requestDTO);

        return CommentDTO.toDTO(addedComment);
    }

}
