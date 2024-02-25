package chabssaltteog.balance_board.dto;

import chabssaltteog.balance_board.domain.post.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDTO {

    @Schema(description = "Tag ID", example = "3")
    private Long tagId;

    @Schema(description = "태그 내용", example = "이직")
    private String tagName;


    public static TagDTO toDTO(Tag tag) {
        return TagDTO.builder()
                .tagId(tag.getTagId())
                .tagName(tag.getTagName())
                .build();
    }
}