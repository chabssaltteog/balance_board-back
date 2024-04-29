package chabssaltteog.balance_board.dto.member;

import chabssaltteog.balance_board.domain.member.Level;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PRO_RES_01 : 프로필 응답 DTO")
public class ProfileInfoResponseDTO {

    @Schema(description = "사용자 ID", example = "2")
    private Long userId;

    @Schema(description = "사용자 email", example = "dsa123@naver.com")
    private String email;

    @Schema(description = "사용자 닉네임", example = "몽글몽글")
    private String nickname;

    @Schema(description = "사용자 레벨", example = "레벨2")
    private Level level;

    @Schema(description = "사용자 경험치 점수", example = "35")
    private int experiencePoints;

//    @Schema(description = "프로필 사진", example = "4")
//    private int imageType;

}