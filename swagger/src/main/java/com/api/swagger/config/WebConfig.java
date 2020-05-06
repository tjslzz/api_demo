package com.api.swagger.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/api/**");
//                .excludePathPatterns("/swagger-ui.html")
//                .excludePathPatterns("/configuration/ui")
//                .excludePathPatterns("/swagger-resources")
//                .excludePathPatterns("/configuration/security")
//                .excludePathPatterns("/v2/api-docs")
//                .excludePathPatterns("/error")
//                .excludePathPatterns("/webjars/**")
//                .excludePathPatterns("/**/favicon.ico");
//        filter swagger
    }
}
