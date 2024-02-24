package chabssaltteog.balance_board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import chabssaltteog.balance_board.domain.post.Comment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long commentId;
    private Long userId;
    private String Nickname;
    private String ImageUrl;
    private String content;
    private LocalDateTime created;

    public static CommentDTO toDTO(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUser().getUserId())
                .Nickname(comment.getUser().getNickname())
                .ImageUrl(comment.getUser().getImageUrl())
                .content(comment.getContent())
                .created(comment.getCreated())
                .build();
    }
}
