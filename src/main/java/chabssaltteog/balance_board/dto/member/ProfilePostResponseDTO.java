package chabssaltteog.balance_board.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PRO_RES_03 : 프로필 내 게시글 응답 DTO")
public class ProfilePostResponseDTO {

    @Schema(description = "총 게시글 갯수", example = "43")
    private int totalPosts;

    @Schema(description = "profile post List")
    private List<ProfilePostDTO> profilePosts;

}
