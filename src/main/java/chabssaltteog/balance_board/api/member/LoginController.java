package chabssaltteog.balance_board.api.member;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.dto.LoginRequestDTO;
import chabssaltteog.balance_board.dto.LoginResponseDTO;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.service.member.MemberService;
import chabssaltteog.balance_board.util.JwtToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Login", description = "Login API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
@Slf4j
public class LoginController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Operation(summary = "Login API", description = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = LoginResponseDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Fail",
                    content = {@Content(schema = @Schema(implementation = LoginFailResponse.class))})
    })
    @PostMapping("/login")
    public Object login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {

        try {
            JwtToken jwtToken = memberService.signIn(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

            Optional<Member> optionalMember = memberRepository.findByEmail(loginRequestDTO.getEmail());
            Long userId = optionalMember.get().getUserId();
            String imageUrl = optionalMember.get().getImageUrl();
            String nickname = optionalMember.get().getNickname();

            log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
            log.info("request email = {}, password = {}", loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

            return new LoginResponseDTO(loginRequestDTO.getEmail(), jwtToken, userId, imageUrl, nickname);

        } catch (Exception e) {
            return new LoginFailResponse(loginRequestDTO.getEmail(), loginRequestDTO.getPassword(), e.getMessage());
        }

    }

    @Operation(summary = "Token Login API", description = "Token Refresh & Get User Data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = LoginResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid Token",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/login/token")
    public Object refreshToken(@RequestHeader("Authorization") String token) {
        try {
            log.info("token = {}", token);
            return memberService.getUserInfoAndGenerateToken(token);

        } catch (Exception e) {
            return new ErrorResponse("Invalid token");
        }
    }


    @Data
    @Schema(title = "MEM_RES_04 : 로그인 실패 응답 DTO")
    @AllArgsConstructor
    static class LoginFailResponse {

        @Schema(description = "사용자가 입력한 email", example = "bbb@gmail.com")
        private String email;

        @Schema(description = "사용자가 입력한 password", example = "333!@bb")
        private String password;

        @Schema(description = "Login Fail Message", example = "회원이 아닙니다.")
        private String message;
    }

    @Data
    @AllArgsConstructor
    @Schema(title = "MEM_RES_05 : Token 로그인 실패 응답 DTO")
    static class ErrorResponse {

        @Schema(description = "Error Message", example = "Invalid token")
        private String message;
    }

}
