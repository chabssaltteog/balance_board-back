package chabssaltteog.balance_board.api.member;

import chabssaltteog.balance_board.dto.PostDTO;
import chabssaltteog.balance_board.dto.ProfilePostDTO;
import chabssaltteog.balance_board.dto.ProfileResponseDTO;
import chabssaltteog.balance_board.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
@Tag(name = "Profile Page", description = "Profile Page API")
@Slf4j
public class ProfileController {

    private final MemberService memberService;

    /**
     *  프로필 = 사용자 정보 + 전체 posts (작성한 글 + 투표한 글)
     */

    @Operation(summary = "Get Profile", description = "사용자 프로필 조회")
    @GetMapping("/profile/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = ProfileResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Fail",
                    content = {@Content(schema = @Schema(implementation = ProfileFailResponse.class))})
    })
    public Object profile(@PathVariable Long userId) {
        log.info("==GET User Profile==");
        log.info("요청 userId = {}", userId);
        try {
            return memberService.getProfile(userId);
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ProfileFailResponse(userId, "프로필 조회 실패");
        }
    }


    @Data
    @AllArgsConstructor
    @Schema(title = "PRO_RES_03 : 프로필 조회 실패 응답 DTO")
    static class ProfileFailResponse {

        @Schema(description = "조회 요청한 userId")
        private Long userId;

        @Schema(description = "실패 응답 메세지", example = "프로필 조회 실패")
        private String message;
    }


}
