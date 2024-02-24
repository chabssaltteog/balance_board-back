package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Comment;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.PostDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {
    //todo return값들을 프론트쪽에서 필요한 데이터를 가진 응답DTO로 변환

    private final PostService postService;
    private final VoteService voteService;

    // 모든 게시글
    /*public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }*/
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
//        return posts.stream().map(post -> PostDTO.toDTO(post)).collect(Collectors.toList());
        return posts.stream().map(PostDTO::toDTO).toList();
    }

    // 게시글 상세보기
    public PostDTO getPostByPostId(Long postId) {
        Post post = postService.getPostByPostId(postId);
        return PostDTO.toDTO(post);
    }

    // 카테고리별 게시글
    public List<PostDTO> getPostsByCategory(Category category) {
        List<Post> posts = postService.getPostsByCategory(category);
        return posts.stream().map(PostDTO::toDTO).toList();
    }

    // 최신 게시글
    public List<Post> getLatestPosts() {
        return postService.getLatestPosts(10); // 최신 10개의 게시글 가져오기
    }

    // 게시글에 등록된 최신 댓글 가져오기
    public List<Comment> getLatestCommentsForPost(Long postId) {
        return postService.getLatestCommentsForPost(postId, 2); // 해당 게시글의 최신 2개 댓글
    }

    // 게시글 작성
    public Post createPost(Post post) {
        return postService.createPost(post);
    }

    // 게시글에 댓글 달기
    public Comment addCommentToPost(Long postId, Comment comment) {
        return postService.addCommentToPost(postId, comment);
    }

    // 게시글에 투표 참여
    public void participateVote(Long postId, Long userId, String votedOption) {
        voteService.participateVote(postId, userId, votedOption);
    }


}
