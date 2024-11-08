package io.jyp.crawler.repository;

import io.jyp.crawler.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    List<Member> findByNoticeFlagOrderByIdDesc(boolean noticeFlag);
    long countByNoticeFlag(boolean noticeFlag);

    // 11월 8일 오류로 인해 10명 메일전송 실패해서 만든 함수
    List<Member> findTop10ByNoticeFlagOrderByIdAsc(boolean noticeFlag);
}
