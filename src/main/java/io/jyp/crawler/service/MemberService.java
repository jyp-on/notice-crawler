package io.jyp.crawler.service;

import io.jyp.crawler.entity.Member;
import io.jyp.crawler.repository.MemberRepository;
import jakarta.mail.MessagingException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;

    @Transactional
    public String sendSubscriptionEmail(String email) {
        Optional<Member> existingMember = memberRepository.findByEmail(email);
        if (existingMember.isPresent() && existingMember.get().isNoticeFlag()) {
            return "이미 구독 중입니다.";
        }

        try {
            emailService.sendEmailVerification(email);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
        return "구독 확인 이메일을 전송했습니다.";
    }

    @Transactional
    public String verifyAndSubscribe(String email, String token) {
        boolean isVerified = emailService.verifyEmail(email, token);
        if (isVerified) {
            Member member = memberRepository.findByEmail(email)
                .orElse(Member.builder()
                    .email(email)
                    .noticeType("MAIN")
                    .noticeFlag(true)
                    .build());

            member.setNoticeFlag(true);
            memberRepository.save(member);
            return "구독이 성공적으로 완료되었습니다.";
        }
        return "유효하지 않은 인증 토큰입니다.";
    }

    @Transactional
    public String sendCancellationEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isEmpty()) throw new IllegalArgumentException("구독 중인 이메일이 아닙니다.");
        if (!member.get().isNoticeFlag()) throw new IllegalArgumentException("이미 구독을 취소하였습니다.");

        try {
            emailService.sendEmailVerification(email);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
        return "이메일을 전송했습니다.";
    }

    @Transactional
    public String verifyAndUnsubscribe(String email, String token) {
        boolean isVerified = emailService.verifyEmail(email, token);
        if (isVerified) {
            Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록된 이메일이 없습니다."));
            member.setNoticeFlag(false);
            memberRepository.save(member);
            return "구독이 성공적으로 취소되었습니다.";
        }
        return "유효하지 않은 인증 토큰입니다.";
    }
}
