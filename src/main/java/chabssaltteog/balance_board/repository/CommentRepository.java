package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.post.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
