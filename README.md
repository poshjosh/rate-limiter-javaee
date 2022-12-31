# rate limiter javaee

Light-weight rate limiting library for javaee web resources, based on
[rate-limiter-web-core](https://github.com/poshjosh/rate-limiter-web-core).

Please first read the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

### Usage

__1. Add required rate-limiter properties__

```java
import com.looseboxes.ratelimiter.util.Rate;
import com.looseboxes.ratelimiter.util.Rates;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RateLimitPropertiesImpl implements RateLimitProperties {

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
    ratesMap.put(IdProvider.ofClass().getId(MyResource.class), Rates.of(Rate.ofMinutes(10)));

    return ratesMap;
  }
}
```

__2. Extend `AbstractRateLimiterDynamicFeature`__

This way a rate limiter will be created an automatically applied based on rate limiter related properties and annotations.

```java
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.ResourceLimitingDynamicFeature;

@javax.ws.rs.ext.Provider 
public class MyResourceLimiterDynamicFeature extends ResourceLimitingDynamicFeature {

  @javax.inject.Inject 
  public MyResourceLimiterDynamicFeature(RateLimitProperties properties) {
    super(properties);
  }
}

```

At this point, your application is ready to enjoy the benefits of rate limiting.

__3. Annotate classes and/or methods.__

```java


@Path("/api")
class MyResource {

  // Only 99 calls to this path is allowed per minute
  @GET
  @Path("/greet")
  @Produces("text/plain")
  String greet(String name) {
    return "Hello " + name;
  }
}
```

### Fine-grained configuration of rate limiting

Configure rate limiting as described in the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

When you configure rate limiting using properties, you could:

- Rate limit a class from properties by using the class ID.
  
- Rate limit a method from properties by using the method ID.

```java
public class RateLimitPropertiesImpl implements RateLimitProperties {
  
    final String classId = IdProvider.ofClass().getId(MyResource.class);
  final String methodId = IdProvider.ofClass().getId(MyResource.class.getMethod("greet", String.class));
  
  @Override
  public Map<String, Rates> getRateLimitConfigs() {
    
    Map<String, Rates> ratesMap = new HashMap<>();
    
    // Rate limit a class
    ratesMap.put(methodId, Rates.of(Rate.ofMinutes(10)));
    
    // Rate limit a method
    ratesMap.put(classId, Rates.of(Rate.ofMinutes(10)));
    
    return ratesMap;
  }
}
```

### Manually create and use a ResourceLimiter

```java

import com.looseboxes.ratelimiter.web.core.impl.WebResourceLimiter;
import com.looseboxes.ratelimiter.web.javaee.WebResourceLimiterConfigJavaee;

public class ResourceLimiterProvider {

  public RateLimiter<R> createResourceLimiter(RateLimiterProperties properties) {
    return new WebResourceLimiter<>(
            WebResourceLimiterConfigJavaee.builder().properties(properties).build());
  }
}
```
This way you use the `ResourceLimiter` as you see fit.

### Annotation Specifications

Please read the [annotation specs](https://github.com/poshjosh/rate-limiter-annotation/blob/main/docs/ANNOTATION_SPECS.md). It is concise.

Enjoy! :wink:

