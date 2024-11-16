package io.jyp.crawler.controller;

import io.jyp.crawler.repository.MemberRepository;
import io.jyp.crawler.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final MemberService memberService;

    @GetMapping
    public String indexPage(Model model) {
        long subscriberCount = memberService.getSubscribeMemberCount();
        model.addAttribute("subscriberCount", subscriberCount);
        return "index";
    }

    @GetMapping("/subscription")
    public String subscriptionPage(Model model) {
        long subscriberCount = memberService.getSubscribeMemberCount();
        model.addAttribute("subscriberCount", subscriberCount);
        return "subscription";
    }

    @GetMapping("/un-subscription")
    public String deSubscriptionPage() {
        return "unSubscription";
    }
}
