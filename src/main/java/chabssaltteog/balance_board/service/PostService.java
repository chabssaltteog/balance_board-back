package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.Vote;
import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Comment;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.post.CreateCommentRequestDTO;
import chabssaltteog.balance_board.dto.post.CommentDeleteDTO;
import chabssaltteog.balance_board.repository.CommentRepository;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.repository.PostRepository;
import chabssaltteog.balance_board.repository.VoteRepository;
import chabssaltteog.balance_board.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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

    public List<Post> getPostsByCategory(Category category) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return postRepository.findByCategory(category, sort);

    }

    public Post createPost(Long userId, Post post) {
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User 정보가 없습니다."));
        post.setUser(user);
        return postRepository.save(post);
    }

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

        // 댓글을 게시글에 추가
        post.addComments(comment);

        // 댓글 수 증가
        post.incrementCommentCount();

        return commentRepository.save(comment);
    }

    @Transactional
    public void deletePost(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        Vote vote = voteRepository.findByPostPostId(postId)
                .orElseThrow(() -> new RuntimeException("해당 postId에 대한 투표가 존재하지 않습니다."));

        // Vote 엔티티에서 post 속성에 null 할당
        vote.setPost(null);

        // Vote 엔티티 삭제
        voteRepository.delete(vote);

        if (post != null) {
            post.deletePost();
            postRepository.delete(post);
        }
    }

    @Transactional
    public void deleteComment(CommentDeleteDTO requestDTO){

        Comment comment = commentRepository.findById(requestDTO.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("삭제할 댓글을 찾을 수 없습니다."));

        // 댓글 작성자의 userId 가져오기
        Long commentAuthorId = comment.getUser().getUserId();

        Long currentUserId = requestDTO.getCurrentUserId();

        // 현재 사용자와 댓글 작성자가 같은지 확인
        if (!currentUserId.equals(commentAuthorId)) {
            throw new IllegalArgumentException("현재 사용자는 이 댓글을 삭제할 권한이 없습니다.");
        }

        Post post = comment.getPost();

        if(post != null){
            post.decrementCommentCount();
        }

        commentRepository.delete(comment);
    }
}
