package chabssaltteog.balance_board.dto.oauth;

import chabssaltteog.balance_board.domain.member.Level;
import chabssaltteog.balance_board.util.JwtToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(title = "MEM_RES_07 : 카카오 로그인 성공 응답 DTO")
public class KakaoLoginResponseDTO {

    @Schema(description = "UserId", example = "49")
    private Long userId;

    @Schema(description = "신규 사용자 : 0, 기존 사용자 : 1", example = "0")
    private int result; // 0 -> 신규 , 1 -> 기존 사용자

    @Schema(description = "사용자 email", example = "dsa123@naver.com")
    private String email;

    @Schema(description = "사용자 닉네임", example = "몽글몽글")
    private String nickname;

    @Schema(description = "사용자 레벨", example = "레벨1")
    private int level;

    @Schema(description = "사용자 경험치 점수", example = "25")
    private int experiencePoints;

//    @Schema(description = "프로필 사진", example = "4")
//    private int imageType;

    @Schema(description = "Jwt Token")
    private JwtToken jwtToken;
}
