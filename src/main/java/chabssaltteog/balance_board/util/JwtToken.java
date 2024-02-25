package chabssaltteog.balance_board.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
@Schema(title = "MEM_RES_05 : 로그인 시 받는 Token")
public class JwtToken {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
