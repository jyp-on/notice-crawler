package io.jyp.crawler.repository

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MemberRepositoryTest @Autowired constructor(
    private val memberRepository: MemberRepository
) {

    @Test
    fun countByNoticeFlag() {
        val count = memberRepository.countByNoticeFlag(true)
        println(count)
    }

    @Test
    fun top10ById() {
        val top10Members = memberRepository.findTop10ByNoticeFlagOrderByIdAsc(true)

        top10Members.forEach { member ->
            println("${member.email}, ${member.id}")
        }
    }
}
