package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.Vote;
import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Comment;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.member.ProfilePostDTO;
import chabssaltteog.balance_board.dto.member.ProfilePostResponseDTO;
import chabssaltteog.balance_board.dto.post.CommentDTO;
import chabssaltteog.balance_board.dto.post.CreateCommentRequestDTO;
import chabssaltteog.balance_board.dto.post.CommentDeleteDTO;
import chabssaltteog.balance_board.repository.CommentRepository;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.repository.PostRepository;
import chabssaltteog.balance_board.repository.VoteRepository;
import chabssaltteog.balance_board.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final VoteRepository voteRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public List<Post> getAllPosts() {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return postRepository.findAll(sort);
    }

    public Post getPostByPostId(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public List<CommentDTO> getCommentsByPostId(Long postId, int page){
        if (page < 0) {
            return null;
        }

        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "created"));
        List<Comment> comments = commentRepository.findByPost_PostId(postId, pageRequest);

        return comments.stream()
                .map(CommentDTO::toDTO)
                .toList();
    }

    public List<Post> getPostsByCategory(Category category) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return postRepository.findByCategory(category, sort);

    }

    @Transactional
    public Post createPost(Long userId, Post post) {
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User 정보가 없습니다."));
        post.setUser(user);
        return postRepository.save(post);
    }

    @Transactional
    public Comment addCommentToPost(CreateCommentRequestDTO requestDTO) {
        Long userId = requestDTO.getUserId();
        Long postId = requestDTO.getPostId();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글 정보가 없습니다."));

        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User 정보가 없습니다."));

        Comment comment = Comment.builder()
                .content(requestDTO.getContent())
                .user(user)
                .post(post)
                .build();

        post.addComments(comment);
        post.incrementCommentCount();

        return commentRepository.save(comment);
    }

    @Transactional
    public void deletePost(Long postId, String token) {

        Member member = getMember(token);
        Long userId = member.getUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        Long postUserId = post.getUser().getUserId();
        if (!userId.equals(postUserId)) {
            throw new IllegalArgumentException("게시글을 삭제할 권한이 없습니다.");
        }

        Vote vote = voteRepository.findByPost_PostId(postId)
                .orElseThrow(() -> new RuntimeException("해당 postId에 대한 투표가 존재하지 않습니다."));

        vote.setPost(null);
        voteRepository.delete(vote);

        if (post != null) {
            post.deletePost();
            postRepository.delete(post);
        }
    }

    @Transactional
    public void deleteComment(CommentDeleteDTO requestDTO, String token){

        Comment comment = commentRepository.findById(requestDTO.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("삭제할 댓글을 찾을 수 없습니다."));

        Long commentAuthorId = comment.getUser().getUserId();
        Long currentUserId = requestDTO.getCurrentUserId();

        Member member = getMember(token);
        Long userId = member.getUserId();

        if (!currentUserId.equals(commentAuthorId) || !currentUserId.equals(userId)) {
            throw new IllegalArgumentException("현재 사용자는 이 댓글을 삭제할 권한이 없습니다.");
        }

        Post post = comment.getPost();

        if(post != null){
            post.decrementCommentCount();
        }

        commentRepository.delete(comment);
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
        return optionalMember.get();
    }
}
