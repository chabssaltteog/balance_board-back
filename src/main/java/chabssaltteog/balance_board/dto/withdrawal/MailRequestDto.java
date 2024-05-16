package chabssaltteog.balance_board.dto.withdrawal;

import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.member.ProfilePostDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
//@Setter
public class MailRequestDto {

    @Schema(description = "email", example = "aaa@gamil.com")
    @Email
    @NotBlank(message = "이메일이 없습니다.")
    private String email;
    private String withdrawalReason;

}
