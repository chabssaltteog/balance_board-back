package chabssaltteog.balance_board.domain.vote;

import chabssaltteog.balance_board.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vote_member")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_member_id")
    private Long voteMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;  //사용자가 참여한 투표

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member user;

    @Column(name = "voted_option")
    private String votedOption; // 사용자가 투표한 항목

    public VoteMember(Vote vote, Member user, String votedOption) {
        this.vote = vote;
        this.user = user;
        this.votedOption = votedOption;
    }
}
