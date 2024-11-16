package io.jyp.crawler.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailConfig {
    @Bean(name = ["verificationMailSender"])
    fun verificationMailSender(
        @Value("\${spring.mail.verification.host}") host: String,
        @Value("\${spring.mail.verification.port}") port: Int,
        @Value("\${spring.mail.verification.username}") username: String,
        @Value("\${spring.mail.verification.password}") password: String,
        @Value("\${spring.mail.verification.default-encoding}") encoding: String,
        @Value("\${spring.mail.verification.properties.mail.smtp.auth}") auth: Boolean,
        @Value("\${spring.mail.verification.properties.mail.smtp.starttls.enable}") starttlsEnable: Boolean,
        @Value("\${spring.mail.verification.properties.mail.smtp.starttls.require}") starttlsRequire: Boolean
    ): JavaMailSender {
        return createMailSender(
            host,
            port,
            username,
            password,
            encoding,
            auth,
            starttlsEnable,
            starttlsRequire
        )
    }

    @Bean(name = ["noticeMailSender"])
    fun noticeMailSender(
        @Value("\${spring.mail.notice.host}") host: String,
        @Value("\${spring.mail.notice.port}") port: Int,
        @Value("\${spring.mail.notice.username}") username: String,
        @Value("\${spring.mail.notice.password}") password: String,
        @Value("\${spring.mail.notice.default-encoding}") encoding: String,
        @Value("\${spring.mail.notice.properties.mail.smtp.auth}") auth: Boolean,
        @Value("\${spring.mail.notice.properties.mail.smtp.starttls.enable}") starttlsEnable: Boolean,
        @Value("\${spring.mail.notice.properties.mail.smtp.starttls.require}") starttlsRequire: Boolean
    ): JavaMailSender {
        return createMailSender(
            host,
            port,
            username,
            password,
            encoding,
            auth,
            starttlsEnable,
            starttlsRequire
        )
    }

    private fun createMailSender(
        host: String, port: Int, username: String, password: String, encoding: String,
        auth: Boolean, starttlsEnable: Boolean, starttlsRequire: Boolean
    ): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port
        mailSender.username = username
        mailSender.password = password
        mailSender.defaultEncoding = encoding

        val props = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = auth
        props["mail.smtp.starttls.enable"] = starttlsEnable
        props["mail.smtp.starttls.required"] = starttlsRequire
        props["mail.debug"] = "false"

        return mailSender
    }
}
