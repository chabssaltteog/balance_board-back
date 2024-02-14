package chabssaltteog.balance_board.controller;

import chabssaltteog.balance_board.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login/oauth2/code/google")    //google redirect url
    public ResponseEntity<String> googleLogin(@RequestParam String code) {
        return loginService.getGoogleAccessToken(code);
    }
}
