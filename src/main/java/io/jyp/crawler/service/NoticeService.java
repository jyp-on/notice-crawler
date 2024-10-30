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
                .build());

        // 구독 설정
        member.setNoticeType(request.getNoticeType());
        member.setNoticeFlag(true); // 구독 활성화
        memberRepository.save(member); // 저장

        return "구독이 성공적으로 생성되었습니다.";
    }
}
