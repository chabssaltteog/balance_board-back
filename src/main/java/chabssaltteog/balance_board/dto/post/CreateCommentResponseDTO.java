package chabssaltteog.balance_board.dto.post;

import chabssaltteog.balance_board.domain.post.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "COM_RES_03 : 댓글 작성 응답 DTO")
public class CreateCommentResponseDTO {

    @Schema(description = "댓글 ID", example = "1")
    private Long commentId;

    @Schema(description = "User ID", example = "6")
    private Long userId;

    @Schema(description = "User 닉네임", example = "몽글몽글")
    private String nickname;

    @Schema(description = "댓글 내용", example = "저도 동의합니다.")
    private String content;

    @Schema(description = "댓글 생성 시간", example = "2024-02-26 19:34:28.683605")
    private LocalDateTime created;

    @Schema(description = "레벨 업을 했는지?", example = "true")
    private boolean isLevelUp;

    @Schema(description = "현재 사용자 레벨", example = "4")
    private int updatedLevel;

    public static CreateCommentResponseDTO toDTO(Comment comment) {
        return CreateCommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUser().getUserId())
                .nickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .created(comment.getCreated())
                .build();
    }


}
