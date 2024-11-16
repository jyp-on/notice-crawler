package io.jyp.crawler.service

import io.jyp.crawler.entity.Member
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class NoticeCrawlerServiceTest @Autowired constructor(
    private val noticeCrawlerService: NoticeCrawlerService
) {

    @Test
    fun crawling() {
        noticeCrawlerService.checkTodayNotice()
    }

    @Test
    fun notifyMember() {
        val noticeInfo = "test"
        val members = List(1) { i ->
            Member(id = (i + 1).toLong(), email = "ju${i}_park@naver.com", noticeFlag = true)
        }

        noticeCrawlerService.notifyNoticeMembers(noticeInfo, members)
    }
}
