package com.looseboxes.ratelimiter.javaee.web;

import javax.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface RequestToIdConverter {
    Object convert(HttpServletRequest request);
}
