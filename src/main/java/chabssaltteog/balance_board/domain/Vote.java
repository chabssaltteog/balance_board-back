package chabssaltteog.balance_board.domain;

import chabssaltteog.balance_board.domain.post.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long voteId;

    @OneToOne(mappedBy = "vote", fetch = FetchType.LAZY)    //읽기 전용
    private Post post;

    @Column(nullable = false)
    private String option1;

    @Column(nullable = false)
    private String option2;

    @Column(name = "option1_count")
    private int option1Count;

    @Column(name = "option2_count")
    private int option2Count;

    public void participate(Long userId, String votedOption) {
        if ("option1".equals(votedOption)) {
            option1Count++;
        } else if ("option2".equals(votedOption)) {
            option2Count++;
        } else {
            throw new IllegalArgumentException("Invalid voted option");
        }
    }

}
