package io.jyp.crawler.controller;

import io.jyp.crawler.dto.MemberRequest;
import io.jyp.crawler.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // 알림 플래그를 변경하는 API
    @PutMapping
    public ResponseEntity<String> updateMember(@RequestBody MemberRequest request) {
        String response = memberService.updateMember(request);
        return ResponseEntity.ok(response);
    }
}
