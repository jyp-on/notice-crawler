package io.jyp.crawler.service;

import io.jyp.crawler.dto.SubscriptionRequest;
import io.jyp.crawler.entity.Member;
import io.jyp.crawler.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final MemberRepository memberRepository;


    @Transactional
    public String createSubscription(SubscriptionRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
            .orElse(Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .noticeType(request.getNoticeType())
                .noticeFlag(true)
                .build());

        memberRepository.save(member);
        return "구독이 성공적으로 생성되었습니다.";
    }
}
