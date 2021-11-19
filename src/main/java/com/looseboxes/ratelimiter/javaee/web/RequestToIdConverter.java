package com.looseboxes.ratelimiter.javaee.web;

@FunctionalInterface
public interface RequestToIdConverter<REQUEST> {
    Object convert(REQUEST request);
}
