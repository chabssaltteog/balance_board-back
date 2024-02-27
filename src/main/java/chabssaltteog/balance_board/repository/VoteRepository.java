package chabssaltteog.balance_board.repository;


import chabssaltteog.balance_board.domain.Vote;
import chabssaltteog.balance_board.domain.VoteMember;
import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface VoteRepository extends JpaRepository<Vote, Long> {

}
