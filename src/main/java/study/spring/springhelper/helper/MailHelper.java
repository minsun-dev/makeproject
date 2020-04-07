package study.spring.springhelper.helper;

import javax.mail.MessagingException;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MailHelper {

	// --> import org.springframework.mail.javamail.JavaMailSender;
	JavaMailSender mailSender;
	
	public MailHelper(JavaMailSender sender) {
		this.mailSender = sender;
	}
	
    /**
     * 메일을 발송한다.
     * 
     * @param receiver - 수신자 메일 주소
     * @param subject - 제목
     * @param content - 내용
     * @throws MessagingException
     * */
    // -> import javax.mail.MessagingException;
    public void sendMail(String receiver, String subject, String content)
    		throws Exception{
    	
    	log.debug("----------------------------------------------------");
        log.debug(String.format("To: %s", receiver));
        log.debug(String.format("Subject: %s", subject));
        log.debug(String.format("Content: %s", content));
        log.debug("----------------------------------------------------");
        
        
        // 생성자를 통해 주입된 설정 객체를 원래 형태로 형변환
        JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) this.mailSender;
        // root-context.xml에 설정된 사용자 이름을 가져온다.
        String sender = mailSenderImpl.getUsername();

        // --> import javax.mail.internet.MimeMessage;
        MimeMessage message = mailSender.createMimeMessage();
        // --> import org.springframework.mail.javamail.MimeMessageHelper;
        MimeMessageHelper helper = new MimeMessageHelper(message);
        // 제목, 내용, 수신자, 발신자(이전 버전에서 오류 발생시) 설정
        helper.setSubject(subject);
        helper.setText(content, true);
        helper.setFrom(new InternetAddress(sender));
        helper.setTo(new InternetAddress(receiver));
        
        
        mailSender.send(message);
    }
}
