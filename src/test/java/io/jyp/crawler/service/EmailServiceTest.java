package io.jyp.crawler.service;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    EmailService emailService;

    @Test
    void emailVerify() throws MessagingException {
        String email = "ju0_park@naver.com";
        emailService.sendEmailVerification(email);
    }
}