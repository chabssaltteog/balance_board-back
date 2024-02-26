package chabssaltteog.balance_board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import chabssaltteog.balance_board.domain.post.Comment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "COM_RES_01 : 댓글 작성 및 조회 응답 DTO")
public class CommentDTO {
    private Long commentId;
    private Long userId;
    private String nickname;
    private String imageUrl;
    private String content;
    private LocalDateTime created;

    public static CommentDTO toDTO(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUser().getUserId())
                .nickname(comment.getUser().getNickname())
                .imageUrl(comment.getUser().getImageUrl())
                .content(comment.getContent())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentDTO> toDTOList(List<Comment> comments) {
        return comments.stream()
                .limit(2) // 최대 2개의 댓글
                .map(CommentDTO::toDTO)
                .collect(Collectors.toList());
    }
}
