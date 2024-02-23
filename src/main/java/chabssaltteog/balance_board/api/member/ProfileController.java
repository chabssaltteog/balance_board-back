package chabssaltteog.balance_board.api.member;

import chabssaltteog.balance_board.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class ProfileController {

//    @GetMapping("/{userId}")
//    public ProfileResponse profile(@PathVariable Long userId) {
//
//    }

    @Data
    @AllArgsConstructor
    static class ProfileResponse {
        private Long userId;
        private String email;
        private String nickname;
        private String imageUrl;
        private List<Post> posts = new ArrayList<>();
        // votes
    }
    /**
     * 전체 : 작성한 글 + 투표한 글
     *
     */
}
