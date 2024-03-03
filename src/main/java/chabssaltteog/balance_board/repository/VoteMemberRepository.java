package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.Vote;
import chabssaltteog.balance_board.domain.VoteMember;
import chabssaltteog.balance_board.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteMemberRepository extends JpaRepository<VoteMember, Long> {

    boolean existsByVoteAndUser_UserId(Vote vote, Long userId);

    public List<VoteMember> findByUser(Member user);

}
