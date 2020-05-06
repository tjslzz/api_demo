package com.api.swagger.config;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class RateLimitInterceptor extends AbstractInterceptorAdapter {

    private static final RateLimiter rateLimiter = RateLimiter.create(1);

    @Override
    protected HttpServletRequest preFilter(HttpServletRequest request) {
        if (!rateLimiter.tryAcquire()) {
            log.warn("rate limiting...");
            return null;
        }
        log.info("request success");
        return request;
    }
}
