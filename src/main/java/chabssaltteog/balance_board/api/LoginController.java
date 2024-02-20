package chabssaltteog.balance_board.api;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.service.LoginService;
import chabssaltteog.balance_board.service.MemberService;
import chabssaltteog.balance_board.util.JwtToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Login", description = "Login API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
@Slf4j
public class LoginController {

    private final MemberService memberService;
    private final LoginService loginService;

    @Operation(summary = "Login API", description = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = LoginResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Fail")
    })
    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {

        JwtToken jwtToken = memberService.signIn(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        log.info("request email = {}, password = {}", loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        return new LoginResponse(loginRequestDTO.getEmail(), loginRequestDTO.getPassword(), jwtToken);
    }

    @Data
    @AllArgsConstructor
    public static class LoginRequestDTO {

        @Schema(description = "email", example = "aaa@gamil.com")
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "password", example = "123!@aa")
        private String password;

    }

    @Data
    @AllArgsConstructor
    static class LoginResponse {

        @Schema(description = "사용자가 입력한 email", example = "bbb@gmail.com")
        private String email;

        @Schema(description = "사용자가 입력한 password", example = "333!@bb")
        private String password;

        @Schema(description = "jwt Token", example = "GrantType, accessToken, refreshToken")
        private JwtToken jwtToken;

    }


//    private final CustomOAuth2UserService customOAuth2UserService;

//    @Operation(summary = "Google Login API", description = "구글 페이지로 리다이렉트")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "302", description = "Redirect to Google",
//                    content = @Content(schema = @Schema(implementation = GoogleLoginResponse.class))),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    @GetMapping("/login/oauth2/code/google")
//    public GoogleLoginResponse googleLogin(
//            @Parameter(name = "code", description = "google authorization code", required = true)
//            @RequestParam("code") String code
//
//    ) {
//        String redirectUrl = "http://localhost:3000/join";
//        return new GoogleLoginResponse(redirectUrl);
//    }


//    @Operation(summary = "Login API", description = "추가 정보 기입 여부 확인")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Success",
//            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
//            @ApiResponse(responseCode = "400", description = "Fail")
//    })
//    @GetMapping("/api/user/login")
//    public LoginResponse login(@AuthenticationPrincipal UserDetails userDetails) {
//
//        String email = userDetails.getUsername();
//
//        boolean confirmRequiredInfo = memberService.confirmRequiredInfo(email);
//        LoginResponse response = new LoginResponse(confirmRequiredInfo);
//        return response;
//    }
//
//    @Data
//    @AllArgsConstructor
//    static class GoogleLoginResponse {
//
//        @Schema(description = "리다이렉트 url", example = "/login/oauth2/code/google")
//        private String redirectUrl;
//    }
//



}
