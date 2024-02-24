package chabssaltteog.balance_board.dto;

import chabssaltteog.balance_board.domain.post.Post;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long postId;
    private String imageUrl;
    private String nickname;
    private String title;
    private LocalDateTime created;
    private String category;
    private String content;
    private Integer voteCount;
    private String option1;
    private String option2;
    private Integer option1Count;
    private Integer option2Count;
    private List<CommentDTO> comments;

    public static PostDTO toDTO(Post post) {
        return PostDTO.builder()
                .postId(post.getPostId())
                .imageUrl(post.getUser().getImageUrl())
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
                .comments(post.getComments().stream().map(comment -> CommentDTO.toDTO(comment)).collect(Collectors.toList())) // Comment List=> CommentDTO List
                .build();
    }

    public static PostDTO toDetailDTO(Post post){
        return PostDTO.builder()
                .postId(post.getPostId())
                .imageUrl(post.getUser().getImageUrl())
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
                .comments(post.getComments().stream().map(comment -> CommentDTO.toDTO(comment)).collect(Collectors.toList())) // Comment List=> CommentDTO List
                .build();
    }
}



