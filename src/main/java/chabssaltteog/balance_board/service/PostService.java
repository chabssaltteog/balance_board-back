package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.domain.post.Like;
import chabssaltteog.balance_board.domain.vote.Vote;
import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Comment;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.post.*;
import chabssaltteog.balance_board.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.NoSuchElementException;
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
    private final LikeRepository likeRepository;

    public Page<Post> getAllPosts(int page) {
        PageRequest pageRequest = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "created"));
        return postRepository.findAll(pageRequest);
    }

    public List<Post> getHotPosts() {
        PageRequest topThree = PageRequest.of(0, 3);
        return postRepository.findHotPosts(topThree);
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
    public CreateCommentResponseDTO addCommentToPost(CreateCommentRequestDTO requestDTO) {
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
        CreateCommentResponseDTO commentResponseDTO = CreateCommentResponseDTO.toDTO(comment);

        if (preLevel != updatedLevel) {
            commentResponseDTO.setLevelUp(true);
            commentResponseDTO.setUpdatedLevel(updatedLevel);
        } else {
            commentResponseDTO.setLevelUp(false);
            commentResponseDTO.setUpdatedLevel(updatedLevel);
        }

        return commentResponseDTO;
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

    @Transactional
    public LikeHateResponseDTO likeOrHatePost(LikeHateRequestDTO likeHateDTO) throws Exception {
        Long postId = likeHateDTO.getPostId();
        Long userId = likeHateDTO.getUserId();
        String action = likeHateDTO.getAction();   // like or hate or cancel

        Optional<Like> optionalLike = likeRepository.findByUser_UserIdAndPost_PostId(userId, postId);
        Optional<Member> optionalMember = memberRepository.findById(userId);
        Optional<Post> optionalPost = postRepository.findById(postId);

        if (optionalMember.isEmpty()) {
            throw new NotFoundException("사용자 정보가 맞지 않습니다.");
        } Member member = optionalMember.get();   // 좋아요를 누른 member

        if (optionalPost.isEmpty()) {
            throw new NotFoundException("게시글 정보가 맞지 않습니다.");
        } Post post = optionalPost.get();         // 좋아요를 누른 post

        if (optionalLike.isEmpty()) {   // First Time
            if (action.equals("like")) {
                likeRepository.save(new Like(member, post, true));
                post.incrementLikeCount();
                return LikeHateResponseDTO.builder()
                        .likeCount(post.getLikeCount())
                        .hateCount(post.getHateCount())
                        .build();
            } else if (action.equals("hate")) {
                likeRepository.save(new Like(member, post, false));
                post.incrementHateCount();
                return LikeHateResponseDTO.builder()
                        .likeCount(post.getLikeCount())
                        .hateCount(post.getHateCount())
                        .build();
            } else throw new IllegalAccessException("cancel을 할 것이 없습니다.");
        }
        Like like = optionalLike.get();
        boolean isLike = like.isLike(); // true or false

        if (isLike == true) {   // 좋아요로 저장된 상태
            if (action.equals("dislike")) { // 좋아요 -> 싫어요로 업데이트
                post.decrementLikeCount();
                post.incrementHateCount();
                like.setLike(false);
            } else if (action.equals("cancel")) {   //좋아요 취소
                post.decrementLikeCount();
                likeRepository.delete(like);
            }
            return LikeHateResponseDTO.builder()
                    .likeCount(post.getLikeCount())
                    .hateCount(post.getHateCount())
                    .build();
        }
        else if (isLike == false) {   // 싫어요로 저장된 상태
            if (action.equals("like")) {  // 싫어요 -> 좋아요로 업데이트
                post.decrementHateCount();
                post.incrementLikeCount();
                like.setLike(true);
            } else if (action.equals("cancel")) {   //싫어요 취소
                post.decrementHateCount();
                likeRepository.delete(like);
            }
            return LikeHateResponseDTO.builder()
                    .likeCount(post.getLikeCount())
                    .hateCount(post.getHateCount())
                    .build();
        }
        return null;
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
