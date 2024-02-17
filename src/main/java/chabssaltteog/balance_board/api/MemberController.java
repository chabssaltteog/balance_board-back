package chabssaltteog.balance_board.api;

import chabssaltteog.balance_board.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<String> registerMember(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestParam String nickname,
                                                 @RequestParam int birthYear,
                                                 @RequestParam String gender) {
        // 현재 인증된 사용자의 이메일 가져오기
        String email = userDetails.getUsername();

        // 사용자 정보 생성 및 업데이트
        memberService.updateMemberInfo(email, nickname, birthYear, gender);

        return ResponseEntity.ok("Member registered successfully");
    }
}
