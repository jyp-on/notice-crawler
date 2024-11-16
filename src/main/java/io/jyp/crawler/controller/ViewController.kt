package io.jyp.crawler.controller

import io.jyp.crawler.service.MemberService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ViewController(
    private val memberService: MemberService,
) {

    @GetMapping
    fun indexPage(model: Model): String {
        val subscriberCount = memberService.subscribeMemberCount
        model.addAttribute("subscriberCount", subscriberCount)
        return "index"
    }

    @GetMapping("/subscription")
    fun subscriptionPage(model: Model): String {
        val subscriberCount = memberService.subscribeMemberCount
        model.addAttribute("subscriberCount", subscriberCount)
        return "subscription"
    }

    @GetMapping("/un-subscription")
    fun deSubscriptionPage(): String {
        return "unSubscription"
    }
}
