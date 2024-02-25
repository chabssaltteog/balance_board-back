package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Comment;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.CreatePostRequestDTO;
import chabssaltteog.balance_board.dto.CreatePostResponseDTO;
import chabssaltteog.balance_board.dto.PostDTO;
import chabssaltteog.balance_board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainService {

    private final PostService postService;
    private final VoteService voteService;
    private final PostRepository postRepository;

    /*public List<PostDTO> getAllPosts() {    // todo 전부 다 보내는게 맞는지?
        List<Post> posts = postService.getAllPosts();
        return posts.stream().map(PostDTO::toDTO).toList(); // 댓글은 최대 두개만
    }*/


    public List<PostDTO> getAllPosts(int pageNumber, int pageSize) {//페이지 사이즈 조정 가능한 메서드
        List<Post> posts = postService.getAllPosts();

        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, posts.size());

        return posts.subList(fromIndex, toIndex)
                .stream()
                .map(PostDTO::toDTO)
                .toList();
    }


    // 게시글 상세보기
    public PostDTO getPostByPostId(Long postId) {
        Post post = postService.getPostByPostId(postId);
        return PostDTO.toDTO(post);
    }

    // 카테고리별 게시글
    /*public List<PostDTO> getPostsByCategory(Category category) {
        List<Post> posts = postService.getPostsByCategory(category);
        return posts.stream().map(PostDTO::toDTO).toList();
    }*/

    public List<PostDTO> getPostsByCategory(Category category, int pageNumber, int pageSize) {
        List<Post> posts = postService.getPostsByCategory(category);
        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, posts.size());

        return posts.subList(fromIndex, toIndex)
                .stream()
                .map(PostDTO::toDTO)
                .toList();
    }


    // 최신 게시글
    public List<Post> getLatestPosts() {
        return postService.getLatestPosts(10); // 최신 10개의 게시글 가져오기
    }

    // 게시글 작성
    public CreatePostResponseDTO createPost(CreatePostRequestDTO requestDTO) {

        Category category = Category.valueOf(requestDTO.getCategory());
        log.info("CREATE POST Category = {}", category);

        Long userId = requestDTO.getUserId();

        Post post = Post.builder()
                .title(requestDTO.getTitle())
                .category(category)
                .content(requestDTO.getContent())
                .tags(requestDTO.getTags())
                .build();

        post.setVoteOptions(requestDTO.getOption1(), requestDTO.getOption2());

        Post createdPost = postService.createPost(userId, post);

        return CreatePostResponseDTO.builder()
                .postId(createdPost.getPostId())
                .created(createdPost.getCreated())
                .userId(userId)
                .build();
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
