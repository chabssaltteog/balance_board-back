package chabssaltteog.balance_board.api.member;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.RefreshToken;
import chabssaltteog.balance_board.dto.member.LoginRequestDTO;
import chabssaltteog.balance_board.dto.member.LoginResponseDTO;
import chabssaltteog.balance_board.exception.TokenNotFoundException;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.repository.RefreshTokenRepository;
import chabssaltteog.balance_board.service.member.MemberService;
import chabssaltteog.balance_board.service.member.RefreshTokenService;
import chabssaltteog.balance_board.util.JwtToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@Tag(name = "Login", description = "Login API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
@Slf4j
public class LoginController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

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
            int imageType = optionalMember.get().getImageType();
            String nickname = optionalMember.get().getNickname();

            // refreshToken을 db에 저장
            refreshTokenService.saveToken(jwtToken.getRefreshToken(), userId);

            log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
            log.info("request email = {}, password = {}", loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

            return new LoginResponseDTO(loginRequestDTO.getEmail(), jwtToken, userId, imageType, nickname);

        } catch (Exception e) {
            return new LoginFailResponse(loginRequestDTO.getEmail(), loginRequestDTO.getPassword(), e.getMessage());
        }

    }

    @Operation(summary = "Token Login API", description = "Token Refresh & Get User Data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = LoginResponseDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid Token",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/login/token")
    public Object refreshToken( //todo token 로그인 시, accessToken은 만료됨 -> refreshToken 써서 accessToken 재발급 받아야함.
            @RequestHeader("Authorization") String accessToken) {   //todo refreshToken도 받아야함
        try {
            log.info("accessToken = {}", accessToken);
            LoginResponseDTO loginResponseDTO = memberService.getUserInfoAndGenerateToken(accessToken);

            return loginResponseDTO;    // todo jwtToken 말고 accessToken 발급으로 변경 / refreshToken expired -> 401 재로그인

        } catch (Exception e) {
            return new ErrorResponse("Invalid token");
        }
    }

//    @GetMapping("/refresh")
//    public JwtToken refresh(HttpServletRequest request, Authentication authentication) {
//        validateExistHeader(request);
//        String email = authentication.getName();
//        String refreshToken = request.getHeader("Refresh-Token");
//        Long userId = refreshTokenService.matches(refreshToken, email);
//        JwtToken jwtToken = refreshTokenService.generateToken(authentication);
//        refreshTokenRepository.save(
//                RefreshToken.builder().token(jwtToken.getRefreshToken()).userId(userId).build()
//        );
//        return jwtToken;
//    }
//
//    private void validateExistHeader(HttpServletRequest request) {
//        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        String refreshTokenHeader = request.getHeader("Refresh-Token");
//        if (Objects.isNull(authorizationHeader) || Objects.isNull(refreshTokenHeader)) {
//            throw new TokenNotFoundException("Token is null");
//        }
//    }

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
