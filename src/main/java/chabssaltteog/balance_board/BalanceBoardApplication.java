package chabssaltteog.balance_board;

import chabssaltteog.balance_board.api.member.WithdrawController;
import chabssaltteog.balance_board.dto.withdrawal.MailRequestDto;
import chabssaltteog.balance_board.util.MailUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import java.util.Random;

@SpringBootApplication
public class BalanceBoardApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(BalanceBoardApplication.class, args);

//		MailUtil mailUtil = context.getBean(MailUtil.class);
//
//		// 이메일 발송에 필요한 정보 설정
//		String subject = "테스트 이메일";
////		String body = "안녕하세요, 테스트 이메일입니다.";
//		String body = "<a href=\"https://www.naver.com\" target=\"_blank\" style=\"display:inline-block;height:40px;font-size:14px;color:blue;text-decoration:underline;\">\n" +
//				"  <button>클릭하세요</button>\n" +
//				"</a>";
//		String to = "k27022000@gmail.com";
//
//		// 이메일 발송 시도
//		boolean result = mailUtil.sendEmail(subject, body, to);
//
////		/////////////////////////////////////////////////
////		WithdrawController withdrawControll = new WithdrawController();
////		MailRequestDto withdrawDto = new MailRequestDto(to);
////		boolean result = withdrawControll.sendmail(withdrawDto);
//
//		// 발송 결과 출력
//		if (result) {
//			System.out.println("이메일이 성공적으로 발송되었습니다.");
//		} else {
//			System.out.println("이메일 발송에 실패했습니다.");
//		}
	}
}
