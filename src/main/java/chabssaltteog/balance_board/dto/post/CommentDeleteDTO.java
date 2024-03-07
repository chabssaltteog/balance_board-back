package chabssaltteog.balance_board.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "COM_REQ_02 : 댓글 삭제 요청 DTO")
public class CommentDeleteDTO {

    @Schema(description = "삭제할 댓글 ID", example = "7")
    private Long commentId;

    @Schema(description = "사용자 ID", example = "23")
    private Long currentUserId;
}
