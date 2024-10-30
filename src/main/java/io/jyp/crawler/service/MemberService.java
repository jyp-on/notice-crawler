package io.jyp.crawler.service;

import io.jyp.crawler.dto.SubscriptionRequest;
import io.jyp.crawler.entity.Member;
import io.jyp.crawler.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public String updateMember(SubscriptionRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 요청에 따라 Member 정보 업데이트
        if (request.getName() != null) {
            member.setName(request.getName());
        }
        if (request.getNoticeType() != null) {
            member.setNoticeType(request.getNoticeType());
        }

        member.setNoticeFlag(request.isNoticeFlag());
        memberRepository.save(member); // 저장
        return "정보가 성공적으로 업데이트되었습니다.";
    }
}
