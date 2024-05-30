package chabssaltteog.balance_board.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "POS_REQ_02 : 좋아요/싫어요 요청 DTO")
public class LikeHateRequestDTO {

    @Schema(description = "사용자 ID", example = "6")
    private Long userId;

    @Schema(description = "좋아요/싫어요 누른 post ID", example = "11")
    private Long postId;

    @Schema(description = "like, dislike, cancel", example = "dislike")
    private String action;     // 좋아요, 싫어요, 취소
}
