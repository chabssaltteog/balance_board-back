package chabssaltteog.balance_board.api;

import chabssaltteog.balance_board.service.MemberService;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
        // 현재 인증된 사용자의 이메일 가져오기
        String email = userDetails.getUsername();

        // 사용자 정보 생성 및 업데이트
        Long userId = memberService.updateMemberInfo(email, request.nickname, request.BirthYear, request.gender);

        return new CreateMemberResponse(userId);

    }

    @GetMapping("/validate")
    public ValidateResponse validateNickname(@RequestBody String nickname) {

    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@AuthenticationPrincipal UserDetails userDetails) {
        // 현재 인증된 사용자의 이메일 가져오기
        String email = userDetails.getUsername();

        // 사용자 정보 확인하여 추가 정보가 있는지 확인
        boolean additionalInfoRequired = memberService.additionalInfoRequired(email);

        // 추가 정보가 필요한 경우 추가 정보 입력 페이지로 리다이렉트
        if (additionalInfoRequired) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/additional-info-page")
                    .build();
        } else {
            // 추가 정보가 없는 경우 기존 서비스 페이지로 리다이렉트
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/main-service-page")
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    static class RegisterResponse {
        private Long userId;
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


}
