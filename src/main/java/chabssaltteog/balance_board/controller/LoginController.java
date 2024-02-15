package chabssaltteog.balance_board.controller;

import chabssaltteog.balance_board.service.CustomOAuth2UserService;
import chabssaltteog.balance_board.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final CustomOAuth2UserService customOAuth2UserService;

    @GetMapping("/login/oauth2/code/google")    //google redirect url
    public ResponseEntity<String> googleLogin(@RequestParam String code) {
        //서비스 호출 및 db 저장
    }
}
