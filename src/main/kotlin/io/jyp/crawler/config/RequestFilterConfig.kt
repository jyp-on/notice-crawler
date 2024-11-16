package io.jyp.crawler.config

import io.jyp.crawler.filter.RequestFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

@Configuration
class RequestFilterConfig {
    @Bean
    fun customFilter(): FilterRegistrationBean<RequestFilter> {
        val registrationBean = FilterRegistrationBean<RequestFilter>()
        registrationBean.filter = RequestFilter()
        registrationBean.addUrlPatterns("/*")
        registrationBean.order = Ordered.HIGHEST_PRECEDENCE // 가장 먼저 실행되도록 설정
        return registrationBean
    }
}