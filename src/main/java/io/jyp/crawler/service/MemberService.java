package io.jyp.crawler.service;

import io.jyp.crawler.dto.SubscriptionRequest;
import io.jyp.crawler.entity.Member;
import io.jyp.crawler.repository.MemberRepository;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    @Transactional
    public String updateMember(SubscriptionRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 요청에 따라 Member 정보 업데이트
        if (StringUtils.hasText(request.getName())) {
            member.setName(request.getName());
        }
        if (StringUtils.hasText(request.getNoticeType())) {
            member.setNoticeType(request.getNoticeType());
        }

        member.setNoticeFlag(request.isNoticeFlag());
        memberRepository.save(member);
        return "정보가 성공적으로 업데이트되었습니다.";
    }


}
