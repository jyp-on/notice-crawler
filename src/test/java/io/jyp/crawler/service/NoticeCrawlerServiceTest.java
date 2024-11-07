package io.jyp.crawler.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NoticeCrawlerServiceTest {

    @Autowired
    private NoticeCrawlerService noticeCrawlerService;

    @Test
    void crawling() {
        noticeCrawlerService.checkTodayNotice();
    }
}