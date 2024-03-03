package chabssaltteog.balance_board.dto.vote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteFailResponseDTO {
    private String userId;
    private Long voteId;
    private String message;
}
