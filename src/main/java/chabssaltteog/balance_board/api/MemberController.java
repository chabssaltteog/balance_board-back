package chabssaltteog.balance_board.api;

import chabssaltteog.balance_board.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Register", description = "Register API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Member Register API", description = "최종 회원가입 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
            content = {@Content(schema = @Schema(implementation = CreateMemberResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Fail")
    })
    @PostMapping("/register")
    public CreateMemberResponse register(@AuthenticationPrincipal UserDetails userDetails,
                                                        @RequestBody @Valid CreateMemberRequest request) {

        String email = userDetails.getUsername();
        Long userId = memberService.updateMemberInfo(email, request.getNickname(), request.getBirthYear(), request.getGender());
        return new CreateMemberResponse(userId);

    }

    @Operation(summary = "Nickname validate API", description = "닉네임 중복 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
            content = {@Content(schema = @Schema(implementation = ValidateResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Fail")
    })
    @GetMapping("/validate")
    public ValidateResponse validateNickname(
            @Parameter(name = "nickname", description = "Parameter Value", example = "몽글몽글", required = true)
            @RequestParam String nickname) {

        boolean isDuplicate = memberService.validateDuplicateMember(nickname);
        ValidateResponse response = new ValidateResponse(isDuplicate);
        return response;
    }

    @Data
    @AllArgsConstructor
    @Schema(title = "MEM_REQ_01 : 회원 가입 요청 DTO")
    static class CreateMemberRequest {

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 3, max = 15, message = "닉네임은 3글자 이상 15글자 이하로 입력해야 합니다.")
        @Schema(description = "닉네임", example = "몽글몽글")
        private String nickname;

        @Schema(description = "출생 년도", example = "1999")
        @NotBlank
        private int BirthYear;


        @Schema(description = "성별", example = "male")
        @NotBlank
        private String gender;

    }

    @Data
    static class CreateMemberResponse {

        @Schema(description = "사용자 ID", example = "1")
        private Long userId;

        public CreateMemberResponse(Long userId) {
            this.userId = userId;
        }
    }

    @Data
    @AllArgsConstructor
    static class ValidateResponse {

        @Schema(description = "중복 여부", example = "true")
        private boolean duplicate;

    }


}
