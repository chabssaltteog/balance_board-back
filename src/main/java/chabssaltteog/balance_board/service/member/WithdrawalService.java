package chabssaltteog.balance_board.service.member;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.dto.withdrawal.MailRequestDto;
import chabssaltteog.balance_board.exception.InvalidUserException;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.util.MailUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

/*@Component
@RequiredArgsConstructor
@Slf4j
public class WithdrawalService {

    private final MailUtil mailUtil;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public boolean sendMail(MailRequestDto requestDto, Authentication authentication) {    //메일 전송 메서드
        try {
            return mailUtil.sendEmail(requestDto, authentication);
        } catch (InvalidUserException e) {
            log.error("Invalid user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean withdraw(int withdrawalCode){    //회원 탈퇴 정보 업데이트

        try {
            if (isValidCode(withdrawalCode)) {
                Member member = memberRepository.findByWithdrawalCode(withdrawalCode);
                if (member != null) {
                    member.withdraw();
                    memberRepository.save(member);
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("Error withdrawing member: {}", e.getMessage());
        }
        return false;
    }

    private boolean isValidCode(int withdrawalCode){    //유효한 탈퇴코드인지 검사
        try {
            Member member = memberRepository.findByWithdrawalCode(withdrawalCode);
            return member != null;
        } catch (Exception e) {
            log.error("Error validating withdrawal code: {}", e.getMessage());
            return false;
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void autoDelete(){      //매일 자정에 탈퇴 30일 지난 회원 이메일 삭제
        try {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            List<Member> withdrawnMembers = memberRepository.findByWithdrawnDateBefore(thirtyDaysAgo);
            for (Member member : withdrawnMembers) {
                member.setEmail(null);
            }
            memberRepository.saveAll(withdrawnMembers);
        } catch (Exception e) {
            log.error("Error in auto-delete task: {}", e.getMessage());
        }
    }
}*/

///////////////////////////

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalService {

    private final MailUtil mailUtil;
    private final MemberRepository memberRepository;
    private final MemberService memberService;  // Ensure MemberService is injected

    public boolean sendMail(MailRequestDto requestDto, Authentication authentication) {
        try {
            // 내부 서버로 요청 보내기 전에 탈퇴 코드를 생성하고 회원에게 저장
            String email = memberService.getUserEmail(authentication);
            int withdrawalCode = memberService.createWithdrawalCode(email);
/*
            requestDto.setEmail(email); // Ensure requestDto has the correct email*/
            boolean emailSent = mailUtil.sendEmail(email, withdrawalCode);
            return emailSent;
        } catch (Exception e) {
            // 로깅 또는 다른 예외 처리
            e.printStackTrace();
            return false;
        }
    }

    public boolean withdraw(int withdrawalCode) {
        if (isValidCode(withdrawalCode)) {
            Member member = memberRepository.findByWithdrawalCode(withdrawalCode);
            log.info("탈퇴회원: {}", member.getEmail());
            if (member != null) {
                member.withdraw();
                memberRepository.save(member);
                return true;
            }
        }
        return false;
    }

    private boolean isValidCode(int withdrawalCode) {
        Member member = memberRepository.findByWithdrawalCode(withdrawalCode);
        return member != null;
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void autoDelete() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Member> withdrawnMembers = memberRepository.findByWithdrawnDateBefore(thirtyDaysAgo);
        for (Member member : withdrawnMembers) {
            member.setEmail(null);
        }
        memberRepository.saveAll(withdrawnMembers);
    }
}
