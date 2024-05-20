package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    public Optional<Member> findByNickname(String nickname);

    public Optional<Member> findByEmail(String email);

    public Member findByUserId(Long userId);

    public Member findByWithdrawalCode(int code);

    List<Member> findByWithdrawnDateBefore(LocalDateTime thirtyDaysAgo);
}
