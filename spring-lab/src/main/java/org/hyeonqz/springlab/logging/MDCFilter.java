package org.hyeonqz.springlab.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class MDCFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            request.getUserPrincipal().getName();
            MDC.put("request-id", "123456789");
            filterChain.doFilter(request, response);
            MDC.clear();
        } catch (Exception e) {
            throw e;
        } finally {
            MDC.clear();
        }

    }
}
