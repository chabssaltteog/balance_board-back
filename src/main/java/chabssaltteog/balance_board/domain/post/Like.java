package chabssaltteog.balance_board.domain.post;

import chabssaltteog.balance_board.domain.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "likes")
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)  // 관계 주인
    @JoinColumn(name = "user_id")
    private Member user;

    @ManyToOne(fetch = FetchType.LAZY)  // 관계 주인
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "is_like")
    private boolean isLike;     // true : 좋아요 , false : 싫어요

    public Like(Member user, Post post, boolean isLike) {
        this.user = user;
        this.post = post;
        this.isLike = isLike;
    }
}
