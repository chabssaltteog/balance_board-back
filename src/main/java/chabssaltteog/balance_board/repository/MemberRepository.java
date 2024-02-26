package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    public Optional<Member> findByNickname(String nickname);    // 닉네임 중복 확인

    public Optional<Member> findByEmail(String email);  //email을 통해 이미 생성된 사용자인지 확인

    public Member findByUserId(Long userId);
}
