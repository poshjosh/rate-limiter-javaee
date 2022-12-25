# rate limiter javaee

Light-weight rate limiting library for javaee web resources, based on
[rate-limiter-web-core](https://github.com/poshjosh/rate-limiter-web-core).

Please first read the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

### Usage

__1. Add required rate-limiter properties__

```yaml
rate-limiter:
  # If using annotations, you have to specify the list packages where resources 
  # that may contain the rate-limit related annotations should be scanned for.
  resource-packages: com.myapplicatioon.web.rest
```

__2. Extend `AbstractRateLimiterDynamicFeature`__

This way a rate limiter will be created an automatically applied based on rate limiter related properties and annotations.

```java
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.AbstractRateLimiterDynamicFeature;

@javax.ws.rs.ext.Provider
public class RateLimiterDynamicFeature
        extends com.looseboxes.ratelimiter.web.javaee.AbstractRateLimiterDynamicFeature {

    @javax.inject.Inject
    public RateLimiterDynamicFeature(RateLimitProperties properties) {
        super(properties);
    }
}

```

__2b. Alternatively, manually create a RateLimiter `__

```java

import com.looseboxes.ratelimiter.web.core.impl.WebRequestRateLimiter;
import com.looseboxes.ratelimiter.web.javaee.WebRequestRateLimiterConfigJavaee;

public class RateLimiterProvider {

  public RateLimiter<R> createRateLimiter(RateLimiterProperties properties) {
    return new WebRequestRateLimiter<>(
            WebRequestRateLimiterConfigJavaee.builder().properties(properties).build());
  }
}
```
This way you use the `RateLimiter` as you see fit.

At this point, your application is ready to enjoy the benefits of rate limiting.

__3. Annotate classes and/or methods.__

```java
import com.looseboxes.ratelimiter.annotations.RateLimit;

@Path("/api")
class GreetingResource {

    // Only 99 calls to this path is allowed per minute
    @RateLimit(limit = 99, duration = 1, timeUnit = TimeUnit.MINUTES)
    @GET
    @Path("/greet")
    @Produces("text/plain")
    String greet(String name) {
        return "Hello " + name;
    }
}
```

__4. Further configure rate limiting__

Configure rate limiting as described in the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

__Notes:__

When you configure rate limiting from the `RateLimitProperties` you implement, you could:

- Configure rate limiting of specific resources from application properties by using the 
  fully qualified class name as the rate-limit group name.

- Narrow the specified properties to a specific method. For example, in this case, by using
  `com.myapplicatioon.web.rest.GreetingResource.greet(java.lang.String)` as the group name.

Enjoy! :wink:

