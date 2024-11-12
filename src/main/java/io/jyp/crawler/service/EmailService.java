package io.jyp.crawler.service;

import io.jyp.crawler.entity.Member;
import io.jyp.crawler.util.HtmlParser;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender noticeMailSender;
    private final JavaMailSender verificationMailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.mail.notice.username}")
    private String noticeSenderEmail;

    @Value("${spring.mail.verification.username}")
    private String verificationSenderEmail;

    // Constructor with @Qualifier annotations
    public EmailService(
        @Qualifier("noticeMailSender") JavaMailSender noticeMailSender,
        @Qualifier("verificationMailSender") JavaMailSender verificationMailSender,
        RedisTemplate<String, String> redisTemplate) {
        this.noticeMailSender = noticeMailSender;
        this.verificationMailSender = verificationMailSender;
        this.redisTemplate = redisTemplate;
    }

    // Method to send notices
    public void sendEmail(Member member, String noticeInfo) throws Exception {
        try {
            MimeMessage message = noticeMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setFrom(noticeSenderEmail);
            helper.setTo(member.getEmail());
            helper.setSubject("[notice-crawler] 오늘의 공지사항 전송드립니다");
            helper.setText(noticeInfo, true);

            noticeMailSender.send(message);
        } catch (Exception e) {
            throw e; // 예외를 상위로 던져서 재시도 로직으로 처리
        }
    }

    // Method to send verification emails
    public void sendEmailVerification(String email) throws MessagingException {
        Random random = new Random();
        String authCode = String.format("%06d", random.nextInt(1000000));
        redisTemplate.opsForValue().set("email:" + email, authCode, Duration.ofMinutes(5));

        MimeMessage message = verificationMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        helper.setFrom(verificationSenderEmail);
        helper.setTo(email);
        helper.setSubject("[notice-crawler] 이메일 인증번호를 알려드립니다.");
        helper.setText(HtmlParser.createVerifyEmailHtml(authCode), true);

        verificationMailSender.send(message);
        log.info("[인증번호 발송] {}", email);
    }

    // Method to verify the email
    public boolean verifyEmail(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get("email:" + email);
        return storedCode != null && storedCode.equals(code);
    }
}
