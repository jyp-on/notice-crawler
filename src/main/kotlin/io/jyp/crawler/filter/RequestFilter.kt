package io.jyp.crawler.filter

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class RequestFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        try {
            // 유효하지 않은 HTTP 메소드 체크
            val method = httpRequest.method
            if (method != null && isValidHttpMethod(method)) {
                chain.doFilter(request, response)
            } else {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST)
            }
        } catch (e: IllegalArgumentException) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST)
        }
    }

    private fun isValidHttpMethod(method: String): Boolean {
        return method.matches("^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS)$".toRegex())
    }
}