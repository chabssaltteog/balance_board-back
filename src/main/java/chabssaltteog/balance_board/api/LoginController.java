package chabssaltteog.balance_board.api;

import chabssaltteog.balance_board.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login", description = "Login API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;

    @Operation(summary = "Login API", description = "추가 정보 기입 여부 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Fail")
    })
    @GetMapping("/login")
    public LoginResponse login(@AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        boolean confirmRequiredInfo = memberService.confirmRequiredInfo(email);
        LoginResponse response = new LoginResponse(confirmRequiredInfo);
        return response;
    }

    @Data
    @AllArgsConstructor
    static class LoginResponse {

        @Schema(description = "추가 기입 필요 여부", example = "true")
        private boolean required;
    }

}
