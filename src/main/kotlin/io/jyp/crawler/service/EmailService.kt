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

    companion object { // 클래스 단위로 로거 정의
        private val log: Logger = LoggerFactory.getLogger(EmailService::class.java)
    }

    @Value("\${spring.mail.notice.username}")
    private lateinit var noticeSenderEmail: String

    @Value("\${spring.mail.verification.username}")
    private lateinit var verificationSenderEmail: String

    @Throws(Exception::class)
    fun sendBulkEmail(membersChunk: List<Member>, noticeInfo: String) {
        try {
            val message = noticeMailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, "utf-8")

            helper.setFrom(noticeSenderEmail)
            helper.setSubject("[notice-crawler] 오늘의 공지사항 전송드립니다")
            helper.setText(noticeInfo, true)

            // BCC(숨은 참조)로 멤버 그룹 추가
            val recipientEmails = membersChunk.map { it.email.trim() }.toTypedArray()
            helper.setBcc(recipientEmails)

            // 이메일 발송
            noticeMailSender.send(message)
            log.info("[Email Send] group size: {}", membersChunk.size)
        } catch (e: Exception) {
            throw e
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
        log.info("[Email Verification Send] {}", email)
    }

    // Method to verify the email
    fun verifyEmail(email: String, code: String): Boolean {
        val storedCode = redisTemplate.opsForValue()["email:$email"]
        return storedCode != null && storedCode == code
    }
}
