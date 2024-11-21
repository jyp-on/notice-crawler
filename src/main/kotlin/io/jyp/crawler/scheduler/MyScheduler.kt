package io.jyp.crawler.scheduler

import io.jyp.crawler.service.NoticeCrawlerService
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class MyScheduler(
    private val crawlerService: NoticeCrawlerService
) {

    private val log: Logger = LoggerFactory.getLogger(MyScheduler::class.java)

    // 매일 20시에 당일 공지사항 전송
    @Scheduled(cron = "0 0 20 * * ?")
    fun crawlingJob() = runBlocking {
        log.info("스케줄 작업 실행: 당일 공지사항 전송")
        crawlerService.checkTodayNotice()
    }
}
