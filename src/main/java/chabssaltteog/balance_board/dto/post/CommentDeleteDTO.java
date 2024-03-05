package chabssaltteog.balance_board.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "댓글 삭제 requestDTO")
@Slf4j
public class CommentDeleteDTO {

    private Long commentId;

    private Long currentUserId;
}
