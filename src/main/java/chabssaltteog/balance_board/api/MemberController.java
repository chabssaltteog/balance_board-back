package chabssaltteog.balance_board.api;

import chabssaltteog.balance_board.service.MemberService;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public CreateMemberResponse register(@AuthenticationPrincipal UserDetails userDetails,
                                                        @RequestBody @Validated CreateMemberRequest request) {

        String email = userDetails.getUsername();
        Long userId = memberService.updateMemberInfo(email, request.nickname, request.BirthYear, request.gender);
        return new CreateMemberResponse(userId);

    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateResponse> validateNickname(@RequestParam String nickname) {

        boolean isDuplicate = memberService.validateDuplicateMember(nickname);
        ValidateResponse response = new ValidateResponse(isDuplicate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        boolean confirmRequiredInfo = memberService.confirmRequiredInfo(email);

        if (confirmRequiredInfo) {
            return ResponseEntity.ok("required");   //추가 기입 필요
        } else {
            return ResponseEntity.ok("non-required");   //필요 X
        }
    }

    @Data
    static class CreateMemberResponse {
        private Long userId;

        public CreateMemberResponse(Long userId) {
            this.userId = userId;
        }


    }
    @Data
    static class CreateMemberRequest {

        @NotEmpty
        private String nickname;
        @NotEmpty
        private int BirthYear;
        @NotEmpty
        private String gender;

    }

    @Data
    static class ValidateResponse {
        private boolean validate;

        public ValidateResponse(boolean validate) {
            this.validate = validate;
        }
    }


}
