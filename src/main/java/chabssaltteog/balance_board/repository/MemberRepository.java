package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    public Optional<Member> findByNickname(String nickname);

    public Optional<Member> findByEmail(String email);

    public Member findByUserId(Long userId);

}
