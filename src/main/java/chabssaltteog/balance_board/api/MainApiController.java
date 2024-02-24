package chabssaltteog.balance_board.api;

import chabssaltteog.balance_board.dto.PostDTO;
import chabssaltteog.balance_board.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainApiController {

    private final MainService mainService;

    @GetMapping("/posts")
    public List<PostDTO> getAllPosts() {
        return mainService.getAllPosts();
    }
}
