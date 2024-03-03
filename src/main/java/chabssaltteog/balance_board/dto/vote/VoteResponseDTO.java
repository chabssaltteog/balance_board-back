package chabssaltteog.balance_board.dto.vote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title = "VOT_RES_01 : 투표 응답 DTO")
public class VoteResponseDTO {

    @Schema(description = "투표 ID == postID", example = "3")
    private Long voteId;

    @Schema(description = "투표한 사용자 ID", example = "6")
    private Long userId;

    @Schema(description = "사용자가 선택한 투표 옵션", example = "살까?")
    private String selectedOption;

    @Schema(description = "옵션 1 Count", example = "11")
    private int option1Count;

    @Schema(description = "옵션 2 Count", example = "15")
    private int option2Count;

}

