package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath(TestApplication.APPLICATION_PATH)
public class TestApplication extends Application {

    public static final String APPLICATION_PATH = "/api";
}