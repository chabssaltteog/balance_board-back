package chabssaltteog.balance_board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private String userImageUrl;
    private String commentUserImageUrl;
    private String commentContent;
    private LocalDateTime commentCreated;
}
