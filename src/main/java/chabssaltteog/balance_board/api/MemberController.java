package chabssaltteog.balance_board.api;

import chabssaltteog.balance_board.service.MemberService;
import chabssaltteog.balance_board.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Register", description = "Register API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final RegisterService registerService;

    @Operation(summary = "test API", description = "test")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = {@Content(schema = @Schema(implementation = TestResult.class))}),
            @ApiResponse(responseCode = "500", description = "fail")
    })
    @GetMapping("/test")
    public TestResult test() {
        TestResult testResult = new TestResult(true, "test message");
        log.info("testResult = {}", testResult);
        return testResult;
    }



    @Operation(summary = "Register API", description = "회원 가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
            content = {@Content(schema = @Schema(implementation = CreateMemberResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Fail")
    })
    @PostMapping("/register")
    public CreateMemberResponse register(
//            @AuthenticationPrincipal UserDetails userDetails,
                                                        @RequestBody @Valid CreateMemberRequestDTO request) {

//        String email = userDetails.getUsername();
        String message = "";
        try {
            registerService.register(request);
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }

        boolean duplicate;
        if (message.isEmpty()) {
            duplicate = false;
        } else duplicate = true;
        log.info("message = {}", message);
        log.info("duplicate = {}", duplicate);

        return new CreateMemberResponse(duplicate, request.getEmail());
//        Long userId = memberService.updateMemberInfo(request.email, request.getNickname(), request.getBirthYear(), request.getGender());

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
        log.info("Nickname: {}", nickname);
        log.info("Is duplicate: {}", isDuplicate);

        return response;
    }

    @Data
    @AllArgsConstructor
    @Schema(title = "MEM_REQ_01 : 회원 가입 요청 DTO")
    public static class CreateMemberRequestDTO {

        @NotBlank
        @Email
        @Schema(description = "email", example = "aaa@gmail.com")
        private String email;

        @NotBlank
        @Schema(description = "password", example = "1231!@aa")
        private String password;

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 3, max = 10, message = "닉네임은 3글자 이상 10글자 이하로 입력해야 합니다.")
        @Schema(description = "닉네임", example = "몽글몽글")
        private String nickname;

        @Schema(description = "출생 년도", example = "1999")
        @NotBlank
        private String BirthYear;


        @Schema(description = "성별", example = "male")
        @NotBlank
        private String gender;

    }

    @Data
    @AllArgsConstructor
    static class CreateMemberResponse {

        @Schema(description = "Email 중복 여부", example = "true")
        private boolean duplicate;

        @Schema(description = "입력한 email", example = "aaa@gmail.com")
        private String email;

    }

    @Data
    @AllArgsConstructor
    static class ValidateResponse {

        @Schema(description = "닉네임 중복 여부", example = "true")
        private boolean duplicate;

    }

    @Data
    @AllArgsConstructor
    static class TestResult {
        @Schema(description = "test용", example = "true")
        private boolean test;

        @Schema(description = "메세지", example = "test message")
        private String message;
    }


}
