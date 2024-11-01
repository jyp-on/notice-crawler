package io.jyp.crawler.repository;

import static org.junit.jupiter.api.Assertions.*;

import io.jyp.crawler.entity.Member;
import io.jyp.crawler.service.NoticeCrawlerService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NoticeCrawlerService noticeCrawlerService;

    @Test
    void getMemberByIdDesc() {
        List<Member> members = memberRepository.findByNoticeTypeAndNoticeFlagOrderByIdDesc("MAIN",
            true);

        for (Member member : members) {
            System.out.println(member.getEmail());
        }
    }

    @Test
    @Rollback(true) // 꼭 DB local로 변경 후 실행
    void insertDummyMembersAndSendEmail() {
        for(int i=0; i<60; i++) {
            Member member = Member.builder()
                .email("ju" + i + "_park@naver.com")
                .noticeFlag(true)
                .noticeType("MAIN")
                .build();
            memberRepository.save(member);
        }

        noticeCrawlerService.checkTodayMainNotice();
    }
}