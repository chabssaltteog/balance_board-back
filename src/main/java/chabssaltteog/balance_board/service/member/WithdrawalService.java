package chabssaltteog.balance_board.service.member;

import chabssaltteog.balance_board.dto.withdrawal.MailRequestDto;
import chabssaltteog.balance_board.util.MailUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final MailUtil mailUtil;

    public boolean sendMail(MailRequestDto requestDto) {
        return mailUtil.sendEmail(requestDto);
    }

}
