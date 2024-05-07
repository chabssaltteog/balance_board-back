package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.domain.vote.Vote;
import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Comment;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.post.CommentDTO;
import chabssaltteog.balance_board.dto.post.CreateCommentRequestDTO;
import chabssaltteog.balance_board.dto.post.CommentDeleteDTO;
import chabssaltteog.balance_board.repository.CommentRepository;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.repository.PostRepository;
import chabssaltteog.balance_board.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final VoteRepository voteRepository;

    public Page<Post> getAllPosts(int page) {
        PageRequest pageRequest = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "created"));
        return postRepository.findAll(pageRequest);
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

    public List<Post> getPostsByCategory(Category category, int page) {

        PageRequest pageRequest = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "created"));
        return postRepository.findByCategory(category, pageRequest);

    }

    @Transactional
    public Post createPost(Long userId, Post post) {
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User 정보가 없습니다."));
        post.setUser(user);
        return postRepository.save(post);
    }

    @Transactional
    public CommentDTO addCommentToPost(CreateCommentRequestDTO requestDTO) {
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

        int preLevel = user.getLevel().getValue();
        int experiencePoints = user.incrementExperiencePoints(2);// 댓글 작성
        int updatedLevel = user.updateLevel(experiencePoints);

        commentRepository.save(comment);
        CommentDTO commentDTO = CommentDTO.toDTO(comment);

        if (preLevel != updatedLevel) {
            commentDTO.setLevelUp(true);
            commentDTO.setUpdatedLevel(updatedLevel);
        } else {
            commentDTO.setLevelUp(false);
            commentDTO.setUpdatedLevel(updatedLevel);
        }

        return commentDTO;
    }

    @Transactional
    public void deletePost(Long postId, Authentication authentication) {

        Member member = getMember(authentication);
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

        post.deletePost();
        postRepository.delete(post);
    }

    @Transactional
    public void deleteComment(CommentDeleteDTO requestDTO, Authentication authentication){

        Comment comment = commentRepository.findById(requestDTO.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("삭제할 댓글을 찾을 수 없습니다."));

        Long commentAuthorId = comment.getUser().getUserId();
        Long currentUserId = requestDTO.getCurrentUserId();

        Member member = getMember(authentication);
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

    private Member getMember(Authentication authentication) {
        String email = authentication.getName();
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다.");
        }
        return optionalMember.get();
    }

}
