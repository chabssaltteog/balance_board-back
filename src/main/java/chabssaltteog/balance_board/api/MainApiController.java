package chabssaltteog.balance_board.api;

import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.dto.PostDTO;
import chabssaltteog.balance_board.service.MainService;
import chabssaltteog.balance_board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainApiController {

    private final MainService mainService;

    private final PostService postService;

    @GetMapping("/posts")//모든 게시글 조회
    public List<PostDTO> getAllPosts() {
        return mainService.getAllPosts();
    }

    @GetMapping("/posts/{postId}")//게시글 상세
    public PostDTO getPost(@PathVariable(name ="postId") Long postId) {
        return mainService.getPostByPostId(postId);
    }

    @GetMapping("/{category}")//카테고리 필터링
    public List<PostDTO> getPostByCategory(@PathVariable(name = "category") Category category) {
        return postService.getPostsByCategory(category);
    }
}
