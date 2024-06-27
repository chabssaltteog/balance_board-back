package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.post.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // 좋아요/싫어요 를 직접적으로 확인
    Optional<Like> findByUser_UserIdAndPost_PostId(Long userId, Long postId);

}
