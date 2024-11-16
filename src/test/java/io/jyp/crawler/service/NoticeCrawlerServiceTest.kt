package io.jyp.crawler.service;

import static org.junit.jupiter.api.Assertions.*;

import io.jyp.crawler.entity.Member;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    void notifyMember() {
        String noticeInfo = "test";
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            members.add(new Member(i+1, "ju_" + i + "park@naver.com", true));
        }
        noticeCrawlerService.notifyNoticeMembers(noticeInfo, members);
    }
}