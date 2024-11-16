package io.jyp.crawler.controller

import io.jyp.crawler.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/subscription")
class SubscriptionController(
    private val memberService: MemberService
) {

    // 구독 생성 요청
    @PostMapping("/request")
    fun requestSubscription(@RequestParam email: String): ResponseEntity<String> {
        val response = memberService.sendSubscriptionEmail(email)
        return ResponseEntity.ok(response)
    }

    // 구독 생성 확인
    @PostMapping
    fun createSubscription(
        @RequestParam email: String,
        @RequestParam token: String
    ): ResponseEntity<String> {
        val response = memberService.verifyAndSubscribe(email, token)
        return ResponseEntity.ok(response)
    }

    // 구독 취소 요청
    @PostMapping("/cancel/request")
    fun requestUnsubscription(@RequestParam email: String): ResponseEntity<String> {
        val response = memberService.sendCancellationEmail(email)
        return ResponseEntity.ok(response)
    }

    // 구독 취소 확인
    @PostMapping("/cancel")
    fun verifyAndUnsubscribe(
        @RequestParam email: String,
        @RequestParam token: String
    ): ResponseEntity<String> {
        val response = memberService.verifyAndUnsubscribe(email, token)
        return ResponseEntity.ok(response)
    }
}
