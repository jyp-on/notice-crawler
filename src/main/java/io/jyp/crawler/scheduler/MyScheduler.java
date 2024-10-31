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

//     매일 오후 6시에 당일 공지사항 전송
//    @Scheduled(cron = "0 0 18 * * ?")
//    public void crawlingJob() {
//        crawlerService.checkLatestMainNotice();
//    }
}
