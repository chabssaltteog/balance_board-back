package chabssaltteog.balance_board.dto.vote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "VOT_REQ_01 : 투표 요청 DTO")
@Builder
public class VoteRequestDTO {
    @Schema(description = "투표할 게시글 ID", example = "3")
    private Long postId;

    @Schema(description = "투표 ID == postID", example = "3")
    private Long voteId;

    @Schema(description = "투표하는 userID", example = "6")
    private Long userId;

    @Schema(description = "사용자가 선택한 투표 옵션", example = "살까?")
    private String selectedOption;


}

