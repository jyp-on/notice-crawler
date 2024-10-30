package io.jyp.crawler.service;

import io.jyp.crawler.entity.Member;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendEmail(Member member, String noticeInfo) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        helper.setFrom(senderEmail);
        helper.setTo(member.getEmail());
        helper.setSubject("새로운 공지사항이 올라왔습니다");
        helper.setText(noticeInfo, true); // HTML 본문 설정

        mailSender.send(message);
    }
}