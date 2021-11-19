package com.looseboxes.ratelimiter.javaee.web;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@javax.inject.Singleton
public class RequestToIdConverterRegistry {

    private static final class DefaultRequestToIdConverter implements RequestToIdConverter<HttpServletRequest>{
        @Override
        public Object convert(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    private final Map<String, RequestToIdConverter> converters;

    @Inject
    public RequestToIdConverterRegistry(RateLimiterConfigurer rateLimiterConfigurer) {
        converters = new HashMap<>();
        if(rateLimiterConfigurer != null) {
            rateLimiterConfigurer.addConverters(this);
        }
    }

    public void registerDefaultConverter(String rateLimiterName) {
        registerConverter(rateLimiterName, new DefaultRequestToIdConverter());
    }

    public void registerConverter(String rateLimiterName, RequestToIdConverter requestToIdConverter) {
        converters.put(rateLimiterName, requestToIdConverter);
    }

    public RequestToIdConverter getConverter(String rateLimiterName) {
        return converters.get(rateLimiterName);
    }
}
