package io.jyp.crawler.repository;

import io.jyp.crawler.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    List<Member> findByNoticeTypeAndNoticeFlagOrderByIdDesc(String noticeType, boolean noticeFlag);
}
