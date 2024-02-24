package chabssaltteog.balance_board.domain.post;

import chabssaltteog.balance_board.domain.BaseTimeEntity;
import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.Vote;
import chabssaltteog.balance_board.dto.PostDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", updatable = false, nullable = false)
    private Long postId;        //pk

    @ManyToOne(fetch = FetchType.LAZY)  //관계 주인
    @JoinColumn(name = "user_id")
    private Member user;    //글 작성 회원

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "vote_id")   //관계 주인
    private Vote vote;

    @OneToMany(mappedBy = "post")
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false, length=30)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "comment_count")
    @ColumnDefault("0")
    private int commentCount;

    @Column(name = "vote_count")
    private int voteCount;


    public void incrementVoteCount() {
        this.voteCount++;
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        this.commentCount--;
    }

}
