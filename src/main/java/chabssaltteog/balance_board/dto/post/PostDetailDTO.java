package chabssaltteog.balance_board.dto.post;

import chabssaltteog.balance_board.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "POS_RES_00 : 상세 게시글 응답 DTO")
public class PostDetailDTO {

    @Schema(description = "게시글 ID", example = "2")
    private Long postId;

    @Schema(description = "user nickname", example = "몽글몽글")
    private String nickname;

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

    @Schema(description = "등록된 투표의 옵션1", example = "살까?")
    private String option1;

    @Schema(description = "등록된 투표의 옵션2", example = "말까?")
    private String option2;

    @Schema(description = "등록된 투표의 옵션1 투표 수", example = "31")
    private Integer option1Count;

    @Schema(description = "등록된 투표의 옵션2 투표 수", example = "15")
    private Integer option2Count;

//    @Schema(description = "게시글에 달린 댓글들")
//    private List<CommentDTO> comments;

    @Schema(description = "게시글에 달린 총 댓글 수", example = "6")
    private Integer commentCount;

    @Schema(description = "태그 목록")
    private List<TagDTO> tags;

    @Schema(description = "사용자가 선택한 투표 옵션", example = "살까?")
    private String selectedVoteOption;

    @Schema(description = "사용자가 선택한 좋아요/싫어요 옵션", example = "hate")
    private String selectedLikeOption;

    @Schema(description = "좋아요 수", example = "12")
    private int likeCount;

    @Schema(description = "싫어요 수", example = "10")
    private int hateCount;



    public static PostDetailDTO toDetailDTO(Post post, String selectedVoteOption, String selectedLikeOption){   //상세 페이지용 - token이 있을 때
        return PostDetailDTO.builder()
                .postId(post.getPostId())
                .nickname(post.getUser().getNickname())
                .title(post.getTitle())
                .created(post.getCreated())
                .category(post.getCategory().name())
                .content(post.getContent())
                .voteCount(post.getVoteCount())
                .option1(post.getVote().getOption1())
                .option2(post.getVote().getOption2())
                .option1Count(post.getVote().getOption1Count())
                .option2Count(post.getVote().getOption2Count())
                .commentCount(post.getCommentCount())
                .tags(post.getTags().stream().map(TagDTO::toDTO).collect(Collectors.toList()))
                .likeCount(post.getLikeCount())
                .hateCount(post.getHateCount())
                .selectedVoteOption(selectedVoteOption)
                .selectedLikeOption(selectedLikeOption)
                .build();
    }

    public static PostDetailDTO toDetailDTO(Post post){   //상세 페이지용 - token 없을 때
        return PostDetailDTO.builder()
                .postId(post.getPostId())
                .nickname(post.getUser().getNickname())
                .title(post.getTitle())
                .created(post.getCreated())
                .category(post.getCategory().name())
                .content(post.getContent())
                .voteCount(post.getVoteCount())
                .option1(post.getVote().getOption1())
                .option2(post.getVote().getOption2())
                .option1Count(post.getVote().getOption1Count())
                .option2Count(post.getVote().getOption2Count())
                /*.comments(post.getComments().stream().map(CommentDTO::toDTO).collect(Collectors.toList())) // 댓글 다 가져옴*/
                .commentCount(post.getCommentCount())
                .tags(post.getTags().stream().map(TagDTO::toDTO).collect(Collectors.toList()))
                .likeCount(post.getLikeCount())
                .hateCount(post.getHateCount())
                .selectedVoteOption(null)
                .selectedLikeOption(null)
                .build();

    }

}
