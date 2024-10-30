package io.jyp.crawler.controller;

import io.jyp.crawler.dto.SubscriptionRequest;
import io.jyp.crawler.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<String> createSubscription(@RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(noticeService.createSubscription(request));
    }


}
