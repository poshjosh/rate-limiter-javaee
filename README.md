# rate limiter javaee

Light-weight rate limiting library for javaee web resources, based on
[rate-limiter-web-core](https://github.com/poshjosh/rate-limiter-web-core).

Please first read the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

### Usage

__1. Implement required injectables__

- `com.looseboxes.ratelimiter.web.core.util.RateLimitProperties`

- `com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer

__2. Add some required properties__

```yaml
rate-limiter:
  # If using annotations, you have to specify the list packages where resources 
  # that may contain the rate-limit related annotations should be scanned for.
  resource-packages: com.myapplicatioon.web.rest
```

__3. Handle RateExceededException.__


__4. Annotate classes/methods. (Optional)__

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

__5. Add some properties (Optional)__

You can configure rate limiting from the properties file.

```yaml
rate-limiter:
  resource-packages: com.myapplicatioon.web.rest
  rate-limit-configs:
    com.myapplicatioon.web.rest.MyResource: # This is the group name
      limits:
        -
          limit: 25
          duration: 1
          timeout: SECONDS
```

By using the fully qualified class name as the group name we can configure rate limiting
of specific resources from application configuration properties.

We could also narrow the specified properties to a specific method. For example, in this case,
by using `com.myapplicatioon.web.rest.MyResource.greet(java.lang.String)` as the group name.

__6. Configure rate limiting__

Configure rate limiting as described in the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

Enjoy! :wink: