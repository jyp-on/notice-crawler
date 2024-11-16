package io.jyp.crawler.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean(name = "verificationMailSender")
    public JavaMailSender verificationMailSender(
        @Value("${spring.mail.verification.host}") String host,
        @Value("${spring.mail.verification.port}") int port,
        @Value("${spring.mail.verification.username}") String username,
        @Value("${spring.mail.verification.password}") String password,
        @Value("${spring.mail.verification.default-encoding}") String encoding,
        @Value("${spring.mail.verification.properties.mail.smtp.auth}") boolean auth,
        @Value("${spring.mail.verification.properties.mail.smtp.starttls.enable}") boolean starttlsEnable,
        @Value("${spring.mail.verification.properties.mail.smtp.starttls.require}") boolean starttlsRequire
    ) {
        return createMailSender(host, port, username, password, encoding, auth, starttlsEnable, starttlsRequire);
    }

    @Bean(name = "noticeMailSender")
    public JavaMailSender noticeMailSender(
        @Value("${spring.mail.notice.host}") String host,
        @Value("${spring.mail.notice.port}") int port,
        @Value("${spring.mail.notice.username}") String username,
        @Value("${spring.mail.notice.password}") String password,
        @Value("${spring.mail.notice.default-encoding}") String encoding,
        @Value("${spring.mail.notice.properties.mail.smtp.auth}") boolean auth,
        @Value("${spring.mail.notice.properties.mail.smtp.starttls.enable}") boolean starttlsEnable,
        @Value("${spring.mail.notice.properties.mail.smtp.starttls.require}") boolean starttlsRequire
    ) {
        return createMailSender(host, port, username, password, encoding, auth, starttlsEnable, starttlsRequire);
    }

    private JavaMailSender createMailSender(String host, int port, String username, String password, String encoding,
        boolean auth, boolean starttlsEnable, boolean starttlsRequire) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding(encoding);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.smtp.starttls.required", starttlsRequire);
        props.put("mail.debug", "false");

        return mailSender;
    }
}
