# rate limiter javaee

Light-weight rate limiting library for javaee web resources, based on
[rate-limiter-web-core](https://github.com/poshjosh/rate-limiter-web-core).

Please first read the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

### Usage

__1. Implement java beans__

- `com.looseboxes.ratelimiter.web.core.util.RateLimitProperties`

- `com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer` (optional)

__2. Implement `DynamicFeature`__

```java
import javax.inject.Named;

import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.AbstractRateLimiterDynamicFeature;
import com.looseboxes.ratelimiter.web.javaee.RateLimiterDynamicFeature;

import javax.ws.rs.container.ContainerRequestContext;

@javax.ws.rs.ext.Provider
public class RateLimiterDynamicFeature extends AbstractRateLimiterDynamicFeature {

  @javax.inject.Inject
  public MyRateLimiterDynamicFeature(RateLimitProperties properties,
                                     @Named("RateLimiter") RateLimiter<ContainerRequestContext> rateLimiter) {
    super(properties, rateLimiter);
  }
}
```

A `RateLimiter` bean is provided by default. Therefore, you could alternatively
implement your own `DynamicFeature` and use the `RateLimiter` bean as you see fit.

__3. Add required rate-limiter properties__

```yaml
rate-limiter:
  # If using annotations, you have to specify the list packages where resources 
  # that may contain the rate-limit related annotations should be scanned for.
  resource-packages: com.myapplicatioon.web.rest
```

__4. Annotate classes and/or methods.__

```java
import com.looseboxes.ratelimiter.annotation.RateLimit;

@Path("/api")
class GreetingResource {

    // Only 99 calls to this path is allowed per minute
    @RateLimit(limit = 99, duration = 1, timeUnit = TimeUnit.MINUTES)
    @GET
    @Path("/greet")
    @Produces("text/plan")
    String greet() {
        return "Hello World!";
    }
}
```

__5. Configure rate limiting__

Configure rate limiting as described in the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

__Notes:__

When you configure rate limiting from the `RateLimitProperties` you implement, you could:

- Configure rate limiting of specific resources from application properties by using the 
  fully qualified class name as the rate-limit group name.

- Narrow the specified properties to a specific method. For example, in this case, by using
  `com.myapplicatioon.web.rest.MyResource.greet(java.lang.String)` as the group name.

Enjoy! :wink:

