package chabssaltteog.balance_board.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import chabssaltteog.balance_board.domain.post.Comment;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "COM_RES_01 : 댓글 작성 및 조회 응답 DTO")
public class CommentDTO {

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

    public static CommentDTO toDTO(Comment comment) {   // 상세 페이지용 -> 댓글 전부 다 보냄
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUser().getUserId())
                .nickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentDTO> toDTOList(List<Comment> comments) {  // 메인 페이지용 -> 댓글 2개만 보냄
        return comments.stream()
                .limit(2) // 최대 2개의 댓글
                .map(CommentDTO::toDTO)
                .collect(Collectors.toList());
    }
}
