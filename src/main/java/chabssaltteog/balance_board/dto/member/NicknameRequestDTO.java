package chabssaltteog.balance_board.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NicknameRequestDTO {

    private String nickname;

    private Long userId;
}
