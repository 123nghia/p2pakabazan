package com.akabazan.partner.internal.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class P2pInternalRequestBodyCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!shouldWrap(request) || request instanceof ContentCachingRequestWrapper) {
            filterChain.doFilter(request, response);
            return;
        }
        ContentCachingRequestWrapper wrapped = new ContentCachingRequestWrapper(request);
        filterChain.doFilter(wrapped, response);
    }

    private static boolean shouldWrap(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith("/internal/p2p/");
    }
}

