package io.jyp.crawler.service;

import io.jyp.crawler.entity.Member;
import io.jyp.crawler.util.HtmlParser;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final RedisTemplate<String, String> redisTemplate;

    public void sendEmail(Member member, String noticeInfo) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        helper.setFrom(senderEmail);
        helper.setTo(member.getEmail());
        helper.setSubject("[jyp.crawler] 오늘의 공지사항 전송드립니다.");
        helper.setText(noticeInfo, true); // HTML 본문 설정

        mailSender.send(message);
    }

    public String sendEmailVerification(String email) throws MessagingException {
        String authCode = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("email:" + email, authCode, Duration.ofMinutes(5));

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setFrom(senderEmail);
        helper.setTo(email);
        helper.setSubject("[jyp.crawler] 인증번호 발송");
        helper.setText(HtmlParser.createVerifyEmailHtml(authCode), true); // HTML 본문 설정
        mailSender.send(message);

        return "인증 이메일이 발송되었습니다.";
    }

    public boolean verifyEmail(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get("email:" + email);
        return storedCode != null && storedCode.equals(code);
    }
}