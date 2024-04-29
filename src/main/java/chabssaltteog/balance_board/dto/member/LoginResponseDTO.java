package chabssaltteog.balance_board.dto.member;

import chabssaltteog.balance_board.util.JwtToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(title = "MEM_RES_03 : 로그인 성공 응답 DTO")
public class LoginResponseDTO {

    @Schema(description = "사용자가 입력한 email", example = "bbb@gmail.com")
    private String email;

    @Schema(description = "jwt Token", example = "GrantType, accessToken, refreshToken")
    private JwtToken jwtToken;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

//    @Schema(description = "사용자 프로필 사진", example = "1")
//    private int imageType;

    @Schema(description = "사용자 닉네임", example = "몽글몽글")
    private String nickname;

}