package io.jyp.crawler.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EmailServiceTest @Autowired constructor(
    private val emailService: EmailService
) {

    @Test
    fun emailVerify() {
        val email = "ju0_park@naver.com"
        emailService.sendEmailVerification(email)
    }
}
