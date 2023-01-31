package com.mentoree.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class TemporalLoggingFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("[LogFilter] > request method : {}", request.getMethod());
        log.info("[LogFilter] > request uri : {}", request.getRequestURI());
        log.info("[LogFilter] > request content type : {}", request.getContentType());

        filterChain.doFilter(request, response);
    }
}
