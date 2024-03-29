package chabssaltteog.balance_board.service.member;

import chabssaltteog.balance_board.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisUtil redisUtil;

    private int authNumber;

    @Value("${spring.mail.username}")
    private String username;

    //임의의 6자리 양수를 반환
    public void makeRandomNumber() {
        Random r = new Random();
        String randomNumber = "";
        for(int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }

        authNumber = Integer.parseInt(randomNumber);
    }

    public String joinEmail(String email) throws MessagingException {
        makeRandomNumber();
        String setFrom = username;
        String toMail = email;
        String title = "밸런스보드 | 회원가입을 위한 인증번호 발송 메일입니다";
        String content =
                "안녕하세요, 밸런스보드입니다." +
                        "<br>" +
                        "저희 사이트에 관심 가져주셔서 감사합니다!" +
                        "<br><br>" +
                        "본 메일은 밸런스보드의 회원가입을 위한 이메일 인증입니다." +
                        "<br>" +
                        "아래의 [이메일 인증번호]를 입력하여 본인확인을 해주시기 바랍니다." +
                        "<br><br>" +
                        "<strong><u>" + authNumber + "</u></strong>";

        mailSend(setFrom, toMail, title, content);
        return Integer.toString(authNumber);
    }

    public void mailSend(String setFrom, String toMail, String title, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        // 이메일 메시지와 관련된 설정
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8"); // true를 전달 -> multipart 형식의 메시지를 지원
        helper.setFrom("Balance Board <" + setFrom + ">");
        helper.setTo(toMail);
        helper.setSubject(title);
        helper.setText(content,true); // true를 설정 -> html 설정
        mailSender.send(message);
        redisUtil.setDataExpire(Integer.toString(authNumber),toMail,60 * 5L);   // 만료 시간 5분
    }

    public boolean CheckAuthNum(String email, String authNum) {
        if(redisUtil.getData(authNum)==null){
            return false;
        }
        else if(redisUtil.getData(authNum).equals(email)){
            return true;
        }
        else{
            return false;
        }
    }

}
