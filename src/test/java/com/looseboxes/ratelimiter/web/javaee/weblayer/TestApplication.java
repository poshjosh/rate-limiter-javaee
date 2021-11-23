package com.looseboxes.ratelimiter.web.javaee.weblayer;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath(TestApplication.APPLICATION_PATH)
public class TestApplication extends Application {
    public static final String APPLICATION_PATH = "/api";
}