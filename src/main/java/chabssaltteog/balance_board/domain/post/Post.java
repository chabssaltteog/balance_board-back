package chabssaltteog.balance_board.domain.post;

import chabssaltteog.balance_board.domain.BaseTimeEntity;
import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.domain.vote.Vote;
import chabssaltteog.balance_board.repository.PostRepository;
import chabssaltteog.balance_board.repository.VoteRepository;
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
@Builder
public class Post extends BaseTimeEntity {

    private transient VoteRepository voteRepository;
    private transient PostRepository postRepository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", updatable = false, nullable = false)
    private Long postId;        //pk

    @ManyToOne(fetch = FetchType.LAZY)  //관계 주인
    @JoinColumn(name = "user_id")
    private Member user;    //글 작성 회원

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vote_id")   //관계 주인
    private Vote vote;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("created DESC")
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
    @ColumnDefault("0")
    private int voteCount;

    @Builder
    public Post(
            // ... (기존 필드 생략)
            VoteRepository voteRepository,
            PostRepository postRepository
    ) {
        // ... (기존 생성자 내용 생략)
        this.voteRepository = voteRepository;
        this.postRepository = postRepository;
    }

    // 투표 관련 메서드
    public void setVoteOptions(String option1, String option2) {
        this.vote = Vote.create(option1, option2);
    }

    public void addTag(Tag tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
        tag.setPost(this);
    }

    public void addComments(Comment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
        comment.setPost(this);
    }

    public void deletePost() {
        if (this.comments != null) {
            for (Comment comment : this.comments) {
                comment.setPost(null);
            }
            this.comments.clear();
        }

        if (this.tags != null) {
            for (Tag tag : this.tags) {
                tag.setPost(null);
            }
            this.tags.clear();
        }

        if (this.vote != null) {
            this.vote.setPost(null);
        }

        this.user = null;

    }

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
