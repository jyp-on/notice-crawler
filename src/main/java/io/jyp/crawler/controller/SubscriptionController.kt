package io.jyp.crawler.controller;

import io.jyp.crawler.dto.SubscriptionRequest;
import io.jyp.crawler.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final MemberService memberService;

    // 구독 생성 요청
    @PostMapping("/request")
    public ResponseEntity<String> requestSubscription(@RequestParam String email) {
        String response = memberService.sendSubscriptionEmail(email);
        return ResponseEntity.ok(response);
    }

    // 구독 생성 확인
    @PostMapping
    public ResponseEntity<String> createSubscription(@RequestParam String email, @RequestParam String token) {
        String response = memberService.verifyAndSubscribe(email, token);
        return ResponseEntity.ok(response);
    }

    // 구독 취소 요청
    @PostMapping("/cancel/request")
    public ResponseEntity<String> requestUnsubscription(@RequestParam String email) {
        String response = memberService.sendCancellationEmail(email);
        return ResponseEntity.ok(response);
    }

    // 구독 취소 확인
    @PostMapping("/cancel")
    public ResponseEntity<String> verifyAndUnsubscribe(@RequestParam String email, @RequestParam String token) {
        String response = memberService.verifyAndUnsubscribe(email, token);
        return ResponseEntity.ok(response);
    }
}
