package com.looseboxes.ratelimiter.web.javaee;

import java.util.Objects;

public final class Assertions {

    @FunctionalInterface
    public interface Executable{
        void execute();
    }

    public static void assertThrows(Class<? extends Exception> exceptionType, Executable executable) {
        try{
            executable.execute();
            throw new AssertionError("Expected to throw: " + exceptionType + ", but did not");
        }catch(Exception caught) {
            if(!exceptionType.isAssignableFrom(caught.getClass())) {
                throw new AssertionError("Expected to throw: " + exceptionType + ", but threw: " + caught);
            }
        }
    }

    public static void assertTrue(boolean flag) {
        if(!flag) {
            throw new AssertionError("Expected: true");
        }
    }

    public static void assertFalse(boolean flag) {
        if(flag) {
            throw new AssertionError("Expected: false");
        }
    }

    public static void assertEqual(Object lhs, Object rhs) {
        if(!Objects.equals(lhs, rhs)) {
            throw new AssertionError("Expected: " + lhs + " to equal: " + rhs);
        }
    }
}
