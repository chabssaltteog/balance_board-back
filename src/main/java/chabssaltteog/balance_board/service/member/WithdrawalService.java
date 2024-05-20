package chabssaltteog.balance_board.service.member;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.dto.withdrawal.MailRequestDto;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.util.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WithdrawalService {

    private final MailUtil mailUtil;
    private final MemberRepository memberRepository;

    public boolean sendMail(MailRequestDto requestDto) {    //메일 전송 메서드
        return mailUtil.sendEmail(requestDto);
    }

    public boolean withdraw(int withdrawalCode){    //회원 탈퇴 정보 업데이트

        if (isValidCode(withdrawalCode)) {
            Member member = memberRepository.findByWithdrawalCode(withdrawalCode);
            if (member != null) {
                member.withdraw();
                memberRepository.save(member);
                return true;
            }
        }
        return false;
    }

    private boolean isValidCode(int withdrawalCode){    //유효한 탈퇴코드인지 검사
        Member member = memberRepository.findByWithdrawalCode(withdrawalCode);
        return member != null;
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void autoDelete(){      //매일 자정에 탈퇴 30일 지난 회원 이메일 삭제
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Member> withdrawnMembers = memberRepository.findByWithdrawnDateBefore(thirtyDaysAgo);
        for(Member member : withdrawnMembers){
            member.setEmail(null);
        }
        memberRepository.saveAll(withdrawnMembers);
    }
}
