package chabssaltteog.balance_board.controller;

import chabssaltteog.balance_board.service.CustomOAuth2UserService;
import chabssaltteog.balance_board.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final MemberService memberService;

    @GetMapping("/api/login")
    public String login() {
        // 로그인 페이지로 리디렉션
        return "Redirecting to login page...";
    }

    @GetMapping("/login/oauth2/code/google")    // Google redirect URL
    public String googleLogin(@AuthenticationPrincipal OAuth2User oauth2User) {
        /**구글 로그인 성공 후 처리할 코드
         *        String email = oauth2User.getAttribute("email");
         *
         *         String nickname = "UserNickname";   //todo
         *         int age = 25;
         *         String gender = "Male";
         *
         *         memberService.updateMemberInfo(email, nickname, age, gender);
         */
        return "redirect:/main";    //todo
    }

    /**
     * 리다이렉션 get 메서드 따로 작성 / 닉네임,나이,성별 값 업데이트하는 메서드 따로 작성
     * or 컨트롤러가 필요 없을 확률 높음 -> SecurityConfig에서 Oauth2LoginSuccessfulHandler 로 칼럼 값 설정하기
     */
}
