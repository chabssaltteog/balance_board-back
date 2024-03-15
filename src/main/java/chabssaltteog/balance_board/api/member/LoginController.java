package chabssaltteog.balance_board.api.member;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.RefreshToken;
import chabssaltteog.balance_board.dto.member.LoginRequestDTO;
import chabssaltteog.balance_board.dto.member.LoginResponseDTO;
import chabssaltteog.balance_board.dto.member.LoginTokenResponseDTO;
import chabssaltteog.balance_board.dto.member.WithdrawalRequestDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
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
            Member member = memberRepository.findByEmail(loginRequestDTO.getEmail())
                    .orElseThrow(() -> new RuntimeException("잘못된 이메일입니다."));
            Long userId = member.getUserId();
            int imageType = member.getImageType();
            String nickname = member.getNickname();

            // refreshToken을 db에 저장
            refreshTokenService.saveToken(jwtToken.getRefreshToken(), userId);

            log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
            log.info("request email = {}, password = {}", loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

            return new LoginResponseDTO(loginRequestDTO.getEmail(), jwtToken, userId, imageType, nickname);

        } catch (Exception e) {
            return new LoginFailResponse(loginRequestDTO.getEmail(), loginRequestDTO.getPassword(), e.getMessage());
        }

    }

    @Operation(summary = "Token Login API", description = "Access Token Refresh & Get User Data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = LoginTokenResponseDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid Token",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/login/token")
    public Object refreshToken(HttpServletRequest request) {

        try {
            validateExistHeader(request);
            String accessToken = resolveToken(request);
            String refreshToken = request.getHeader("Refresh-Token");

            log.info("accessToken = {}", accessToken);
            log.info("refreshToken = {}", refreshToken);

            if (!refreshTokenService.validateRefreshToken(refreshToken)) {
                // refresh 토큰 유효하지X -> 재로그인
                return new ErrorResponse("refresh Token Invalid");
            }
            return memberService.getUserInfoAndGenerateToken(accessToken, refreshToken);

        } catch (Exception e) {
            log.info(e.getMessage());
            return new ErrorResponse("Invalid token");
        }
    }

    @Operation(summary = "Access Token Refresh API", description = "Access Token Refresh")
    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request) {

        try {
            validateExistHeader(request);
            String accessToken = resolveToken(request);
            String refreshToken = request.getHeader("Refresh-Token");

            log.info("accessToken = {}", accessToken);
            log.info("refreshToken = {}", refreshToken);

            if (!refreshTokenService.validateRefreshToken(refreshToken)) {
                // refresh 토큰 유효하지X -> 재로그인
                log.info("재로그인 필요");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("재로그인 필요");
            }

            Authentication authentication = refreshTokenService.getAuthentication(accessToken);
            Long userId = refreshTokenService.matches(refreshToken, authentication.getName());
            log.info("token refresh 요청 : accessToken - userId = {}", userId);

            String newAccessToken = refreshTokenService.generateToken(authentication);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
                    .body("new access token 발급 성공");

        } catch (Exception e) {
            log.info("Token Refresh Fail");
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token Refresh Fail");
        }
    }

    private void validateExistHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshTokenHeader = request.getHeader("Refresh-Token");
        if (Objects.isNull(authorizationHeader) || Objects.isNull(refreshTokenHeader)) {
            throw new TokenNotFoundException("Token is null");
        }
    }

    // Request Header에서 access 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
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
