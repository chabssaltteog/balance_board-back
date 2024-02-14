package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    public Optional<Member> findByName(String name);    // 최초 로그인 여부를 체크하기 위함
}
