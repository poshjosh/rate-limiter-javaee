package io.github.poshjosh.ratelimiter.web.javaee.readme;

import io.github.poshjosh.ratelimiter.annotation.RateId;
import io.github.poshjosh.ratelimiter.model.Rate;
import io.github.poshjosh.ratelimiter.model.Rates;
import io.github.poshjosh.ratelimiter.util.RateLimitProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RateLimitPropertiesImpl implements RateLimitProperties {

    @Override
    public List<Class<?>> getResourceClasses() {
        return Collections.emptyList();
    }

    // If not using annotations, return an empty list
    @Override
    public List<String> getResourcePackages() {
        return Collections.singletonList("com.myapplicatioon.web.rest");
    }

    // If not using properties, return an empty map
    @Override
    public Map<String, Rates> getRateLimitConfigs() {
        Map<String, Rates> ratesMap = new HashMap<>();

        // Accept only 2 tasks per second
        ratesMap.put("task_queue", Rates.of(Rate.ofSeconds(2)));

        // # Cap streaming of video to 5kb per second
        ratesMap.put("video_download", Rates.of(Rate.ofSeconds(5_000)));

        // # Limit requests to this resource to 10 per minute
        ratesMap.put(RateId.of(MyResource.class), Rates.of(Rate.ofMinutes(10)));

        return ratesMap;
    }
}
