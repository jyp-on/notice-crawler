package io.jyp.crawler.service;

import io.jyp.crawler.entity.Member;
import io.jyp.crawler.util.HtmlParser;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@Slf4j
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

    public void sendEmailVerification(String email) throws MessagingException {
        Random random = new Random();
        String authCode = String.format("%06d", random.nextInt(1000000));
        redisTemplate.opsForValue().set("email:" + email, authCode, Duration.ofMinutes(5));

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setFrom(senderEmail);
        helper.setTo(email);
        helper.setSubject("[jyp.crawler] 인증번호 발송");
        helper.setText(HtmlParser.createVerifyEmailHtml(authCode), true); // HTML 본문 설정
        mailSender.send(message);
        log.info("[인증번호 발송] {}", email);
    }

    public boolean verifyEmail(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get("email:" + email);
        return storedCode != null && storedCode.equals(code);
    }
}