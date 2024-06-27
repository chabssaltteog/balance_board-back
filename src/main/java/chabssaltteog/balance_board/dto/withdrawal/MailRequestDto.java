package chabssaltteog.balance_board.dto.withdrawal;

import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.member.ProfilePostDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MailRequestDto {
    private String withdrawalReason;
}
