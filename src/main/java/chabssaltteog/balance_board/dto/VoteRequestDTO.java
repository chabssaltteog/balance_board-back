package chabssaltteog.balance_board.dto;

import chabssaltteog.balance_board.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRequestDTO {
    private String selectedOption;  // 사용자가 선택한 투표 옵션
    private Member user;  // 투표를 하는 사용자 정보

}

