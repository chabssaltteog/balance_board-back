package chabssaltteog.balance_board;

import chabssaltteog.balance_board.api.member.WithdrawController;
import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.dto.withdrawal.MailRequestDto;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.util.MailUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import java.util.Random;

@SpringBootApplication
public class BalanceBoardApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(BalanceBoardApplication.class, args);

	}
}
