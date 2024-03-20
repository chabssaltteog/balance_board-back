package chabssaltteog.balance_board.api.member;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.service.member.MemberService;
import chabssaltteog.balance_board.service.member.RegisterService;
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
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Tag(name = "Register", description = "Register API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final RegisterService registerService;


    @Operation(summary = "Register API", description = "회원 가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
            content = {@Content(schema = @Schema(implementation = CreateMemberResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Fail")
    })
    @PostMapping("/register")
    public Object register(@RequestBody @Valid CreateMemberRequestDTO request) {

        try {
            CreateMemberResponse savedMemberResponse = registerService.register(request);
            boolean duplicate = false;
            log.info("message = {}", "회원 가입 성공");
            return new CreateMemberResponse(duplicate, savedMemberResponse.getEmail(), savedMemberResponse.getUserId(), savedMemberResponse.getImageType(),
                    savedMemberResponse.getNickname(), savedMemberResponse.getBirthYear(), savedMemberResponse.getGender());
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            boolean duplicate = true;
            log.info("message = {}", message);
            return new CreateMemberFailResponse(duplicate, request.getEmail(), request.getNickname(), request.getBirthYear(), request.getGender());
        }

    }


    @Operation(summary = "Nickname validate API", description = "닉네임 중복 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
            content = {@Content(schema = @Schema(implementation = ValidateResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Fail")
    })
    @GetMapping("/validate/nickname")
    public ValidateResponse validateNickname(
            @Parameter(name = "nickname", description = "Parameter Value", example = "몽글몽글", required = true)
            @RequestParam String nickname) {

        boolean isDuplicate = memberService.validateDuplicateNickname(nickname);
        log.info("Nickname: {}", nickname);
        log.info("Is duplicate: {}", isDuplicate);

        return new ValidateResponse(isDuplicate);
    }

    @Operation(summary = "Email validate API", description = "이메일 중복 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
            content = {@Content(schema = @Schema(implementation = ValidateResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Fail")
    })
    @GetMapping("/validate/email")
    public ValidateResponse validateEmail(
            @Parameter(name = "email", description = "Parameter Value", example = "aaa@gmail.com", required = true)
            @RequestParam String email) {

        boolean isDuplicate = registerService.validateDuplicateEmail(email);
        ValidateResponse response = new ValidateResponse(isDuplicate);
        log.info("Email: {}", email);
        log.info("Is duplicate: {}", isDuplicate);

        return response;
    }

    @Data
    @AllArgsConstructor
    @Schema(title = "MEM_REQ_01 : 회원 가입 요청 DTO")
    @Builder
    @NoArgsConstructor
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
        private String birthYear;

        @Schema(description = "성별", example = "male")
        @NotBlank
        private String gender;

        @Builder.Default private List<String> roles = new ArrayList<>();

        public Member toEntity(String encodedPassword, List<String> roles) {
            return Member.builder()
                    .email(email)
                    .password(encodedPassword)
                    .nickname(nickname)
                    .gender(gender)
                    .roles(roles)
                    .birthYear(birthYear)
                    .build();
        }

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "MEM_RES_01 : 회원 가입 응답 DTO")
    public static class CreateMemberResponse {

        @Schema(description = "Email 중복 여부", example = "true")
        private boolean duplicate;

        @Schema(description = "입력한 email", example = "aaa@gmail.com")
        private String email;

        @Schema(description = "사용자 ID", example = "6")
        private Long userId;

        @Schema(description = "사용자 프로필 사진(1~5)", example = "2")
        private int imageType;

        @Schema(description = "사용자 닉네임", example = "몽글몽글")
        private String nickname;

        @Schema(description = "사용자 출생년도", example = "1999")
        private String birthYear;

        @Schema(description = "사용자 성별", example = "male")
        private String gender;

        static public CreateMemberResponse toDto(Member member) {
            return CreateMemberResponse.builder()
                    .userId(member.getUserId())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .birthYear(member.getBirthYear())
                    .imageType(member.getImageType())
                    .gender(member.getGender())
                    .build();
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(title = "MEM_RES_02 : 회원 가입 실패 응답 DTO")
    static class CreateMemberFailResponse {
        @Schema(description = "Email 중복 여부", example = "true")
        private boolean duplicate;

        @Schema(description = "입력한 email", example = "aaa@gmail.com")
        private String email;

        @Schema(description = "사용자 닉네임", example = "몽글몽글")
        private String nickname;

        @Schema(description = "사용자 출생년도", example = "1999")
        private String birthYear;

        @Schema(description = "사용자 성별", example = "male")
        private String gender;
    }

    @Data
    @AllArgsConstructor
    @Schema(title = "MEM_RES_03 : 중복 여부 응답 DTO")
    static class ValidateResponse {

        @Schema(description = "중복 여부", example = "true")
        private boolean duplicate;

    }

}
