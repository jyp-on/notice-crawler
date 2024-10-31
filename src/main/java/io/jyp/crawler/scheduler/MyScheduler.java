package io.jyp.crawler.scheduler;

import io.jyp.crawler.service.NoticeCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class MyScheduler {

    private final NoticeCrawlerService crawlerService;

    // 매일 오전 9시부터 오후 6시까지 1분 간격으로 실행
    @Scheduled(cron = "0 */1 9-18 * * ?")
    public void crawlingJob() {
        crawlerService.checkMainNotice();
        crawlerService.checkSoftNotice();
    }
}
