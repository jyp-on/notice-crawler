package io.jyp.crawler.controller;

import io.jyp.crawler.dto.SubscriptionRequest;
import io.jyp.crawler.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PutMapping
    public ResponseEntity<String> updateMember(@RequestBody SubscriptionRequest request) {
        String response = memberService.updateMember(request);
        return ResponseEntity.ok(response);
    }
}
