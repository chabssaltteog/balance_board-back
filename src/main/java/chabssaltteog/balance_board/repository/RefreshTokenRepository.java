package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.member.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    public Optional<RefreshToken> findByUserId(Long userId);
}
