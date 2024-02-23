package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.VoteMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteMemberRepository extends JpaRepository<VoteMember, Long> {
}
