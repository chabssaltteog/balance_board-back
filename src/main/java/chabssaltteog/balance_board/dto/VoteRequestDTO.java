package chabssaltteog.balance_board.dto;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.Vote;
import chabssaltteog.balance_board.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRequestDTO {
    private Long postId;
    private Long voteId; //투표
    private Long userId;  // 투표를 하는 사용자 정보
    private String selectedOption;  // 사용자가 선택한 투표 옵션


}

