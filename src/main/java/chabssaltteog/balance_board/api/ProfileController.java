package chabssaltteog.balance_board.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class ProfileController {

    @GetMapping("/{userId}")
    public ProfileResponse profile(@PathVariable Long userId) {

    }

    @Data
    @AllArgsConstructor
    static class ProfileResponse {
        private Long userId;
        private String name;
        private String email;
        private int birthYear;
        private String gender;
        private String nickname;
    }
}
