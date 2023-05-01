# rate limiter javaee

Enterprise rate limiter for javaee web applications, based on
[rate-limiter-web-core](https://github.com/poshjosh/rate-limiter-web-core).

We believe that rate limiting should be as simple as:

```java
@Rate(10) // 10 permits per second for all methods in this class
@Path("/api")
public class GreetingResource {

  // GUESTs - 1 call per second, USERs - 3 calls per second 
  @Rate(permits=1, when="web.request.user.role=GUEST")
  @Rate(permits=3, when="web.request.user.role=USER")
  @GET
  @Path("/smile")
  public String smile() {
    return ":)";
  }

  // When system available memory < 1GB - 10 calls per minute
  @Rate(permits=10, timeUnit=TimeUnit.MINUTES, when="sys.memory.available<1gb")
  // When request parameter `who` has value of either ALICE or BOB - 1 permit per second
  @Rate(permits=1, when="web.request.parameter={who=[ALICE|BOB]}")
  @GET
  @Path("/greet")
  public String greet(@QueryParam("who") String who) {
    return "Hello " + who;
  }
}
```

Please first read the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

To add a dependency on `rate-limiter-javaee` using Maven, use the following:

```xml
        <dependency>
            <groupId>io.github.poshjosh</groupId>
            <artifactId>rate-limiter-javaee</artifactId>
            <version>0.5.0</version> 
        </dependency>
```

### Usage

__1. Implement `RateLimitProperties`__

```java
public class RateLimitPropertiesImpl implements RateLimitProperties {

    // If not using annotations, return an empty list
    @Override 
    public List<String> getResourcePackages() {
        return Collections.singletonList("com.myapplicatioon.web.rest");
    }

    // If not using properties, return an empty map
    @Override 
    public Map<String, Rates> getRateLimitConfigs() {
        // Accept only 2 tasks per second
        return Collections.singletonMap("task_queue", Rates.of(Rate.ofSeconds(2)));
    }
}
```

__2. Extend `ResourceLimitingDynamicFeature`__

This way a rate limiter will be created an automatically applied based on rate limiter related properties and annotations.

```java
@javax.ws.rs.ext.Provider
public class DynamicFeatureImpl extends ResourceLimitingDynamicFeature {

    @javax.inject.Inject 
    public DynamicFeatureImpl(RateLimitProperties properties) {
        super(properties);
    }
}

```

At this point, your application is ready to enjoy the benefits of rate limiting.

__3. Annotate classes and/or methods.__

```java
@Path("/api")
class MyResource {

  // Only 25 calls per second for users in role GUEST
  @Rate(25)
  @RateCondition("web.request.user.role=GUEST")
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
  @Override
  public Map<String, Rates> getRateLimitConfigs() {
    
    Map<String, Rates> ratesMap = new HashMap<>();
    
    // Rate limit a class
    ratesMap.put(ElementId.of(MyResource.class), Rates.of(Rate.ofMinutes(10)));
    
    // Rate limit a method
    ratesMap.put(ElementId.of(MyResource.class.getMethod("greet", String.class)), Rates.of(Rate.ofMinutes(10)));
    
    return ratesMap;
  }
}
```

### Expression Language

The expression language allows us to write expressive rate conditions, e.g: 

`@RateCondition("web.request.user.role=GUEST")`

`@RateCondition("sys.memory.free<1GB")`

| format          | example                                  | description |
|-----------------|------------------------------------------|-------------|
| LHS=RHS         | web.request.header=X-RateLimit-Limit     | true, when the X-RateLimit-Limit header exists |
| LHS={key=val}   | web.request.parameter={limited=true}     | true, when request parameter limited equals true |
| LHS=[AlB]       | web.request.user.role=[GUESTlRESTRICTED] | true, when the user role is either GUEST or RESTRICTED |
| LHS=[A&B]       | web.request.user.role=[GUEST&RESTRICTED] | true, when the user role is either GUEST and RESTRICTED |
| LHS={key=[AlB]} | web.request.header={name=[val_0lval_1]}  | true, when either val_0 or val_1 is set a header |
| LHS={key=[A&B]} | web.request.header={name=[val_0&val_1]}  | true, when both val_0 and val_1 are set as headers |

__Note:__ `|` represents OR, while `&` represents AND

A rich set of conditions may be expressed as detailed in the 
[web specification](https://github.com/poshjosh/rate-limiter-web-core/blob/master/docs/RATE-CONDITION-EXPRESSION-LANGUAGE.md).

### Manually create and use a ResourceLimiter

Usually, you are provided with appropriate `ResourceLimiter`s based on the annotations
and properties you specify. However, you could manually create and use `ResourceLimiters`.

```java
import ResourceLimiterRegistryJavaee;

public class ResourceLimiterProvider {

    public ResourceLimiter createResourceLimiter() {
        return ResourceLimiterRegistryJavaee.ofDefaults().createResourceLimiter();
    }
}
```
This way you use the `ResourceLimiter` as you see fit.

### Annotation Specifications

Please read the [annotation specs](https://github.com/poshjosh/rate-limiter-annotation/blob/main/docs/ANNOTATION_SPECS.md). It is concise.

Enjoy! :wink:

