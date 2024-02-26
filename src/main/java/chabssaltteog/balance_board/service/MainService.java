package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Comment;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.domain.post.Tag;
import chabssaltteog.balance_board.dto.*;
import chabssaltteog.balance_board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainService {

    private final PostService postService;
    private final VoteService voteService;

    // 메인 페이지
    public List<PostDTO> getAllPosts(int pageNumber, int pageSize) {
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
        return PostDTO.toDetailDTO(post);
    }

    // 게시글 카테고리 필터링
    public List<PostDTO> getPostsByCategory(Category category, int pageNumber, int pageSize) {
        List<Post> posts = postService.getPostsByCategory(category);
        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, posts.size());

        return posts.subList(fromIndex, toIndex)
                .stream()
                .map(PostDTO::toDTO)
                .toList();
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
    public CommentDTO addCommentToPost(CreateCommentRequestDTO requestDTO) {

        Comment addedComment = postService.addCommentToPost(requestDTO);

        return CommentDTO.toDTO(addedComment);
    }

    // 게시글에 투표 참여
    public void participateVote(VoteRequestDTO voteRequestDTO) {
        voteService.participateVote(voteRequestDTO);
    }


}
