package chabssaltteog.balance_board.dto.member;

import chabssaltteog.balance_board.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PRO_RES_02 : 프로필 내 게시글 응답 DTO")
public class ProfilePostDTO {

    @Schema(description = "게시글 ID", example = "2")
    private Long postId;

    @Schema(description = "게시글 제목", example = "있잖아..")
    private String title;

    @Schema(description = "게시글 생성 시간", example = "2024-02-25T10:15:30")
    private LocalDateTime created;

    @Schema(description = "게시글 카테고리", example = "라이프")
    private String category;

    @Schema(description = "게시글 내용", example = "...")
    private String content;

    @Schema(description = "총 투표 수", example = "46")
    private Integer voteCount;

    @Schema(description = "투표 게시글 확인", example = "false")
    private boolean isVoted;

    @Schema(description = "작성 게시글 확인", example = "true")
    private boolean isWrited;


    public static ProfilePostDTO toDTO(Post post, boolean isWrited, boolean isVoted) {
        return ProfilePostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .created(post.getCreated())
                .category(post.getCategory().name())
                .content(post.getContent())
                .voteCount(post.getVoteCount())
                .isWrited(isWrited)
                .isVoted(isVoted)
                .build();
    }
}
