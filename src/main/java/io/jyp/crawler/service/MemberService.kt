package io.jyp.crawler.service

import io.jyp.crawler.entity.Member
import io.jyp.crawler.repository.MemberRepository
import jakarta.mail.MessagingException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val emailService: EmailService
) {

    private val log = LoggerFactory.getLogger(MemberService::class.java)

    @Transactional
    fun getSubscribeMemberCount(): Long {
        return memberRepository.countByNoticeFlag(true)
//        return 500
    }

    @Transactional
    fun sendSubscriptionEmail(email: String): String {
        val existingMember = memberRepository.findByEmail(email)
        if (existingMember.isPresent && existingMember.get().noticeFlag) {
            throw IllegalArgumentException("이미 구독 중입니다.")
        }

        if (getSubscribeMemberCount() >= 500) {
            throw IllegalArgumentException("구독자 수가 최대치를 초과하여 구독할 수 없습니다.")
        }

        try {
            emailService.sendEmailVerification(email)
        } catch (e: MessagingException) {
            throw RuntimeException("이메일 전송에 실패했습니다.", e)
        }
        return "구독 확인 이메일을 전송했습니다."
    }

    @Transactional
    fun verifyAndSubscribe(email: String, token: String): String {
        if (getSubscribeMemberCount() >= 500) {
            throw IllegalArgumentException("구독자 수가 최대치를 초과하여 구독할 수 없습니다.")
        }

        val isVerified = emailService.verifyEmail(email, token)
        if (isVerified) {
            val member = memberRepository.findByEmail(email)
                .orElse(Member(email = email, noticeFlag = true))

            member.noticeFlag = true
            memberRepository.save(member)
            log.info("[구독] $email")
            return "구독이 성공적으로 완료되었습니다."
        }
        throw IllegalArgumentException("유효하지 않은 인증 토큰입니다.")
    }

    @Transactional
    fun sendCancellationEmail(email: String): String {
        val member = memberRepository.findByEmail(email)
        if (member.isEmpty) throw IllegalArgumentException("구독 중인 이메일이 아닙니다.")
        if (!member.get().noticeFlag) throw IllegalArgumentException("이미 구독을 취소하였습니다.")

        try {
            emailService.sendEmailVerification(email)
        } catch (e: MessagingException) {
            throw RuntimeException("이메일 전송에 실패했습니다.", e)
        }
        return "이메일을 전송했습니다."
    }

    @Transactional
    fun verifyAndUnsubscribe(email: String, token: String): String {
        val isVerified = emailService.verifyEmail(email, token)
        if (isVerified) {
            val member = memberRepository.findByEmail(email)
                .orElseThrow { IllegalArgumentException("등록된 이메일이 없습니다.") }
            member.noticeFlag = false
            memberRepository.save(member)
            log.info("[구독취소] $email")
            return "구독이 성공적으로 취소되었습니다."
        }
        throw IllegalArgumentException("유효하지 않은 인증 토큰입니다.")
    }
}
