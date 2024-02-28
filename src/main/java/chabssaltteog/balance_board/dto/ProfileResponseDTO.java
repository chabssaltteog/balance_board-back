package chabssaltteog.balance_board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PRO_RES_01 : 프로필 응답 DTO")
public class ProfileResponseDTO {

    @Schema(description = "사용자 ID", example = "2")
    private Long userId;

    @Schema(description = "사용자 email", example = "dsa123@naver.com")
    private String email;

    @Schema(description = "사용자 닉네임", example = "몽글몽글")
    private String nickname;

    @Schema(description = "프로필 사진", example = "4")
    private int imageType;

    @Schema(description = "사용자가 작성한 글 리스트")
    private List<ProfilePostDTO> userPosts;

    @Schema(description = "사용자가 투표한 글 리스트")
    private List<ProfilePostDTO> votedPosts;

}