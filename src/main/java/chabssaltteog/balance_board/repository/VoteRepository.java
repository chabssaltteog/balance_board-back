package chabssaltteog.balance_board.repository;


import chabssaltteog.balance_board.domain.vote.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByPost_PostId(Long postId);
}
