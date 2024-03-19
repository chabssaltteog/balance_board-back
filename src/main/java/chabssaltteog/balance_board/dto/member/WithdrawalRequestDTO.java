package chabssaltteog.balance_board.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "회원 탈퇴 DTO")
public class WithdrawalRequestDTO {

    @Schema(description = "탈퇴할 회원 비밀번호", example = "password!@12")
    private String password;
}
