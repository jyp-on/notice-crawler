package io.jyp.crawler.repository

import io.jyp.crawler.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByEmail(email: String?): Optional<Member>
    fun findByNoticeFlagOrderByIdDesc(noticeFlag: Boolean): List<Member>
    fun countByNoticeFlag(noticeFlag: Boolean): Long

    // 11월 8일 오류로 인해 10명 메일전송 실패해서 만든 함수
    fun findTop10ByNoticeFlagOrderByIdAsc(noticeFlag: Boolean): List<Member>
}
