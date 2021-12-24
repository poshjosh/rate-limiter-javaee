# rate limiter javaee

Light-weight rate limiting library for javaee web resources, based on
[rate-limiter-web-core](https://github.com/poshjosh/rate-limiter-web-core).

Please first read the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

### Usage

__1. Extend `RateLimiterDynamicFeature`__

```java
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.web.core.ResourceClassesSupplier;
import com.looseboxes.ratelimiter.web.javaee.*;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;

@Provider
public class MyRateLimiterDynamicFeature extends RateLimiterDynamicFeature {

    @Inject
    public MyRateLimiterDynamicFeature(RateLimiter<ContainerRequestContext> rateLimiter, ResourceClassesSupplier resourceClassesSupplier) {
        super(rateLimiter, resourceClassesSupplier);
    }
}
```

A `RateLimiter` bean is provided by default. Therefore, you could alternatively
implement your own `WebMvcConfigurer` and use the `RateLimiter` bean as you see fit.

__2. Implement injectables__

- `com.looseboxes.ratelimiter.web.core.util.RateLimitProperties`

- `com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer` (optional)

__3. Add required rate-limiter properties__

```yaml
rate-limiter:
  # If using annotations, you have to specify the list packages where resources 
  # that may contain the rate-limit related annotations should be scanned for.
  resource-packages: com.myapplicatioon.web.rest
```

__4. Handle RateExceededException.__

TODO

__5. Annotate classes and/or methods.__

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

__6. Configure rate limiting__

Configure rate limiting as described in the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

__Notes:__

When you configure rate limiting from the `RateLimitProperties` you implement:

- By using the fully qualified class name as the group name we can configure rate limiting
  of specific resources from application configuration properties.

- You could also narrow the specified properties to a specific method. For example, in this case,
  by using `com.myapplicatioon.web.rest.MyResource.greet(java.lang.String)` as the group name.

If you create a `org.springframework.cache.Cache` named `com.looseboxes.ratelimiter.web.spring.cache`
(the default cache name), it will be used to create the default `RateCache`.

Enjoy! :wink:

