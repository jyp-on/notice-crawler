package io.jyp.crawler.service

import io.jyp.crawler.entity.Member
import io.jyp.crawler.util.HtmlParser
import jakarta.mail.MessagingException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class EmailService(
    @Qualifier("noticeMailSender") private val noticeMailSender: JavaMailSender,
    @Qualifier("verificationMailSender") private val verificationMailSender: JavaMailSender,
    private val redisTemplate: RedisTemplate<String, String>
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(EmailService::class.java)
    }

    @Value("\${spring.mail.notice.username}")
    private lateinit var noticeSenderEmail: String

    @Value("\${spring.mail.verification.username}")
    private lateinit var verificationSenderEmail: String

    // Method to send notices
    @Throws(Exception::class)
    fun sendEmail(member: Member, noticeInfo: String) {
        try {
            val message = noticeMailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, "utf-8")

            helper.setFrom(noticeSenderEmail)
            helper.setTo(member.email)
            helper.setSubject("[notice-crawler] 오늘의 공지사항 전송드립니다")
            helper.setText(noticeInfo, true)

            noticeMailSender.send(message)
        } catch (e: Exception) {
            throw e // 예외를 상위로 던져서 재시도 로직으로 처리
        }
    }

    // Method to send verification emails
    @Throws(MessagingException::class)
    fun sendEmailVerification(email: String) {
        val random = Random()
        val authCode = String.format("%06d", random.nextInt(1000000))
        redisTemplate.opsForValue().set("email:$email", authCode, Duration.ofMinutes(5))

        val message = verificationMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, "utf-8")

        helper.setFrom(verificationSenderEmail)
        helper.setTo(email)
        helper.setSubject("[notice-crawler] 이메일 인증번호를 알려드립니다.")
        helper.setText(HtmlParser.createVerifyEmailHtml(authCode), true)

        verificationMailSender.send(message)
        log.info("[인증번호 발송] {}", email)
    }

    // Method to verify the email
    fun verifyEmail(email: String, code: String): Boolean {
        val storedCode = redisTemplate.opsForValue()["email:$email"]
        return storedCode != null && storedCode == code
    }
}
