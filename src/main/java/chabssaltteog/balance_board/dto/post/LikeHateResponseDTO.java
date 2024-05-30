package chabssaltteog.balance_board.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(title = "POS_RES_03 : 좋아요/싫어요 응답 DTO")
public class LikeHateResponseDTO {

    @Schema(description = "좋아요 수", example = "122")
    private int likeCount;

    @Schema(description = "싫어요 수", example = "53")
    private int hateCount;
}
