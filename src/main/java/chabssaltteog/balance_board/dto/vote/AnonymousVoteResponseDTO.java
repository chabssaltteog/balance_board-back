package chabssaltteog.balance_board.dto.vote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "VOT_RES_03 : 비회원 투표 응답 DTO")
public class AnonymousVoteResponseDTO {

    @Schema(description = "옵션 1 Count", example = "11")
    private int option1Count;

    @Schema(description = "옵션 2 Count", example = "15")
    private int option2Count;
}
