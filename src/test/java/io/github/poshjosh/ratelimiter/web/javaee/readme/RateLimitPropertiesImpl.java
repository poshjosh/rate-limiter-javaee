package io.github.poshjosh.ratelimiter.web.javaee.readme;

import io.github.poshjosh.ratelimiter.annotation.RateId;
import io.github.poshjosh.ratelimiter.model.Rate;
import io.github.poshjosh.ratelimiter.model.Rates;
import io.github.poshjosh.ratelimiter.util.RateLimitProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RateLimitPropertiesImpl implements RateLimitProperties {

    @Override
    public List<Class<?>> getResourceClasses() {
        return Collections.emptyList();
    }

    // If not using annotations, return an empty list
    @Override
    public List<String> getResourcePackages() {
        return Collections.singletonList("com.myapp.web.rest");
    }

    // If not using properties, return an empty map
    @Override
    public List<Rates> getRates() {
        List<Rates> ratesList = new ArrayList<>();

        // Accept only 2 tasks per second
        ratesList.add(Rates.of("task_queue", Rate.ofSeconds(2)));

        // # Cap streaming of video to 5kb per second
        ratesList.add(Rates.of("video_download", Rate.ofSeconds(5_000)));

        // # Limit requests to this resource to 10 per minute
        ratesList.add(Rates.of(RateId.of(MyResource.class), Rate.ofMinutes(10)));

        return ratesList;
    }
}
