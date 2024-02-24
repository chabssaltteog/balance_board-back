package chabssaltteog.balance_board.dto;

import chabssaltteog.balance_board.domain.post.Post;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long postId;
    private String writerImageUrl;
    private String writerNickName;
    private String title;
    private LocalDateTime createdAt;
    private String postCategory;
    private String postContent;
    private String voteOptionOne;
    private String voteOptionTwo;
    private List<CommentDTO> comments;

    public static PostDTO toDTO(Post post) {
        return PostDTO.builder()
                .postId(post.getPostId())
//                .writerImageUrl(post.getUser().getProfileImageUrl())
//                .writerNickName(post.getUser().getNickName())
                .title(post.getTitle())
                .createdAt(post.getCreated())
                .postCategory(post.getCategory().name())
                .postContent(post.getContent())
//                .voteOptionOne(post.getVote().getOptionOne())
//                .voteOptionTwo(post.getVote().getOptionTwo())
//                .comments(CommentDTO.toDTOList(post.getComments()))
                .build();
    }
}



