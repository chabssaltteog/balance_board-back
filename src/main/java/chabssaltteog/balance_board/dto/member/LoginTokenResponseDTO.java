package chabssaltteog.balance_board.dto.member;

import chabssaltteog.balance_board.domain.member.Level;
import chabssaltteog.balance_board.util.JwtToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title = "MEM_RES_06 : Token 로그인 성공 응답 DTO")
public class LoginTokenResponseDTO {

    @Schema(description = "사용자가 입력한 email", example = "bbb@gmail.com")
    private String email;

    @Schema(description = "new accessToken", example = "accessToken")
    private String accessToken;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 레벨", example = "레벨2")
    private int level;

    @Schema(description = "사용자 경험치 점수", example = "35")
    private int experiencePoints;

//    @Schema(description = "사용자 프로필 사진", example = "1")
//    private int imageType;

    @Schema(description = "사용자 닉네임", example = "몽글몽글")
    private String nickname;

}
