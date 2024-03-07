package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.post.Comment;
import chabssaltteog.balance_board.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_PostId(Long postId, Pageable pageable);
}
