package chabssaltteog.balance_board.api.member;

import chabssaltteog.balance_board.dto.member.ProfilePostDTO;
import chabssaltteog.balance_board.dto.member.ProfileInfoResponseDTO;
import chabssaltteog.balance_board.dto.member.ProfilePostResponseDTO;
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
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "User Profile Info", description = "사용자 프로필 - 정보")
    @GetMapping("/profile/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = ProfileInfoResponseDTO.class))}),
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


    @Operation(summary = "User Profile Posts", description = "사용자 프로필 - 게시글")
    @GetMapping("/profile/{userId}/posts")
    public Object profilePosts(
            @PathVariable Long userId,
            @RequestParam(name = "listType") int listType,
            @RequestParam(name = "page") int page
    ) {
        try {
            return memberService.getProfilePosts(userId, listType, page);
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ProfileFailResponse(userId, "프로필 posts 조회 실패");
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
