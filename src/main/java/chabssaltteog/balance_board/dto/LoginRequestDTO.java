package chabssaltteog.balance_board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(title = "MEM_REQ_02 : 로그인 요청 DTO")
public class LoginRequestDTO {

    @Schema(description = "email", example = "aaa@gamil.com")
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "password", example = "123!@aa")
    private String password;

}
