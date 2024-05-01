package chabssaltteog.balance_board.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "PRO_REQ_01 : 닉네임 변경 요청 DTO")
public class NicknameRequestDTO {

    @Schema(description = "변경하는 닉네임", example = "몽글몽글")
    private String nickname;

    @Schema(description = "사용자 ID", example = "3")
    private Long userId;
}
