package io.jyp.crawler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping
    public String indexPage() {
        return "index";
    }

    @GetMapping("/subscription")
    public String subscriptionPage() {
        return "subscription";
    }

    @GetMapping("/un-subscription")
    public String deSubscriptionPage() {
        return "unSubscription";
    }
}
