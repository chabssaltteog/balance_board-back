package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    public Optional<Member> findByName(String nickname);    // 닉네임 중복 확인

    public Optional<Member> findByProviderId(Long googleId);
}
