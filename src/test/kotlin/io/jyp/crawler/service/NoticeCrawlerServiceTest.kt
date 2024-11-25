package io.jyp.crawler.service

import io.jyp.crawler.entity.Member
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class NoticeCrawlerServiceTest @Autowired constructor(
    private val noticeCrawlerService: NoticeCrawlerService
) {

    @Test
    fun `공지사항 크롤링 테스트`() = runTest {
        noticeCrawlerService.checkTodayNotice()
    }

    @Test
    fun `공지사항 크롤링 테스트 - 더미유저`() {
        val noticeInfo = "test"
        val members = List(110) { i ->
            Member(id = (i + 1).toLong(), email = "ju${i}_park@naver.com", noticeFlag = true)
        }

        noticeCrawlerService.notifyNoticeMembers(noticeInfo, members)
    }
}
