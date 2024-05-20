package chabssaltteog.balance_board.util;

import chabssaltteog.balance_board.dto.withdrawal.MailRequestDto;
import chabssaltteog.balance_board.service.member.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
public class MailUtil {     //이메일 전송관련 클래스

    private final MemberService memberService;

    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private Integer port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.properties.mail.smtp.from}")
    private String emailFrom;

    public MailUtil(MemberService memberService) {
        this.memberService = memberService;
    }

    public boolean sendEmail(MailRequestDto mailRequestDto) {

        int withdrawalCode = memberService.createRandomCode();

        String subject = "테스트 이메일";
        String body = "<a href=\"http://localhost:8081/api/withdrawal?withdrawalCode=" + withdrawalCode + "  <button>클릭하세요</button>\n" +
                "</a>";

        // Send email
        Properties prop = new Properties();
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.starttls.required", "true");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailFrom));            //수신자메일주소
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailRequestDto.getEmail()));

            // Subject
            message.setSubject(subject); //메일 제목을 입력

            // Text
//            message.setText(body);    //메일 내용을 입력
            message.setContent(
                    body, "text/html; charset=utf-8"
            );
            // send the message
            Transport.send(message); ////전송
            return true;
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}


