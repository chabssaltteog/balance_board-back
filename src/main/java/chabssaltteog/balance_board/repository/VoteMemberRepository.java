package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.domain.vote.Vote;
import chabssaltteog.balance_board.domain.vote.VoteMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteMemberRepository extends JpaRepository<VoteMember, Long> {

    boolean existsByVoteAndUser_UserId(Vote vote, Long userId);

    public List<VoteMember> findByUser(Member user);

}
