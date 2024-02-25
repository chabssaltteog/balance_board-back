package chabssaltteog.balance_board.dto;

import chabssaltteog.balance_board.domain.post.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "POS_REQ_01 : 게시글 작성 요청 DTO")
@Builder
public class CreatePostRequestDTO {

    @NotNull
    @Schema(description = "게시글 작성자 ID", example = "6")
    private Long userId;

    @NotBlank
    @Schema(description = "게시글 제목", example = "있잖아..")
    private String title;

    @NotBlank
    @Schema(description = "게시글 카테고리", example = "라이프")
    private String category;

    @NotBlank
    @Schema(description = "게시글 내용", example = "...")
    private String content;

    @Size(max = 5)
    @Schema(description = "게시글 태그들", example = "눈, 날씨, 겨울")
    private List<String> tags = new ArrayList<>();

    @NotBlank
    @Schema(description = "등록된 투표의 옵션1", example = "살까?")
    private String option1;

    @NotBlank
    @Schema(description = "등록된 투표의 옵션2", example = "말까?")
    private String option2;
}
