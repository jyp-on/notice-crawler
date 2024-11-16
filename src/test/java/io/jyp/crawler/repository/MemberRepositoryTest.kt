package io.jyp.crawler.repository;

import static org.junit.jupiter.api.Assertions.*;

import io.jyp.crawler.entity.Member;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void countByNoticeFlag() {
        long count = memberRepository.countByNoticeFlag(true);
        System.out.println(count);
    }

    @Test
    void top10ById() {
        List<Member> top10ByNoticeFlagOrderByIdAsc = memberRepository.findTop10ByNoticeFlagOrderByIdAsc(
            true);

        for (Member member : top10ByNoticeFlagOrderByIdAsc) {
            System.out.printf("%s, %d\n", member.getEmail(), member.getId());
        }
    }
}