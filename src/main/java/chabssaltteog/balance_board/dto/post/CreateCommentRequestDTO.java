package chabssaltteog.balance_board.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "COM_REQ_01 : 댓글 작성 요청 DTO")
@Builder
public class CreateCommentRequestDTO {

    @Schema(description = "댓글 작성 userID", example = "2")
    private Long userId;

    @Schema(description = "댓글을 다는 게시글의 postID", example = "1")
    private Long postId;

    @Schema(description = "댓글 내용", example = "저도 동의합니다..")
    private String content;
}
