package chabssaltteog.balance_board.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title = "POS_RES_01 : 게시글 작성 응답 DTO")
public class CreatePostResponseDTO {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "게시글 작성 일시", example = "2024-02-25T10:15:30")
    private LocalDateTime created;

    @Schema(description = "게시글 작성자 ID", example = "6")
    private Long userId;

    @Schema(description = "레벨업을 했는지", example = "true")
    private boolean isLevelUp;

    @Schema(description = "현재 사용자 레벨", example = "4")
    private int updatedLevel;

}