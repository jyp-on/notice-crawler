package io.jyp.crawler.filter;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // 유효하지 않은 HTTP 메소드 체크
            String method = httpRequest.getMethod();
            if (method != null && isValidHttpMethod(method)) {
                chain.doFilter(request, response);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private boolean isValidHttpMethod(String method) {
        return method.matches("^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS)$");
    }
}