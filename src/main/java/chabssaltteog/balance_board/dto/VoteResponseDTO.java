package chabssaltteog.balance_board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteResponseDTO {

    private Long voteId;

    private Long userId;

    private String selectedOption;

    /*private int option1Count;

    private int option2Count;*/

}

