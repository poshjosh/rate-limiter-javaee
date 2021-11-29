package com.looseboxes.ratelimiter.web.javaee.uri;

import org.glassfish.jersey.uri.PathPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

final class PatternMatchers {

    private static final Logger LOG = LoggerFactory.getLogger(PatternMatchers.class);

    private static String format(String s, boolean needsLeadingSlash) {
        if(needsLeadingSlash) {
            if(!s.startsWith("/")) {
                return "/" + s;
            }
        }else {
            if(s.startsWith("/")) {
                return s.substring(1);
            }
        }
        return s;
    }

    private static final class JavaPatternMatcher implements PatternMatcher {
        private final String rawPattern;
        private final boolean hasLeadingSlash;
        private final Pattern pattern;
        private JavaPatternMatcher(String rawPattern, Pattern pattern) {
            this.rawPattern = Objects.requireNonNull(rawPattern);
            this.hasLeadingSlash = rawPattern.startsWith("/");
            this.pattern = Objects.requireNonNull(pattern);
        }
        @Override
        public boolean matches(String s) {
            s = format(s, hasLeadingSlash);
            final boolean matches = pattern.matcher(s).matches();
            if(LOG.isTraceEnabled()) {
                LOG.trace("Matches: {}, text: {}, pattern: {}", matches, s, rawPattern);
            }
            return matches;
        }
        public String getPattern() {
            return rawPattern;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            JavaPatternMatcher that = (JavaPatternMatcher) o;
            return rawPattern.equals(that.rawPattern);
        }
        @Override
        public int hashCode() {
            return Objects.hash(rawPattern);
        }

        @Override
        public String toString() {
            return "JavaPatternMatcher{" + rawPattern + "}";
        }
    }

    private static final class ZeroOrMorePathSegmentsMatcher implements PatternMatcher {
        private final String rawPattern;
        private final boolean hasLeadingSlash;
        private final PathPattern pathPattern;
        private ZeroOrMorePathSegmentsMatcher(String rawPattern, PathPattern pathPattern) {
            this.rawPattern = Objects.requireNonNull(rawPattern);
            this.hasLeadingSlash = rawPattern.startsWith("/");
            this.pathPattern = Objects.requireNonNull(pathPattern);
        }
        @Override
        public boolean matches(String s) {
            s = format(s, hasLeadingSlash);
            final MatchResult matchResult = pathPattern.match(s);
            final boolean matches = matchResult != null && matchResult.groupCount() > 0;
            if(LOG.isTraceEnabled()) {
                LOG.trace("Matches: {}, text: {}, pattern: {}", matches, s, rawPattern);
            }
            return matches;
        }
        public String getPattern() {
            return rawPattern;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ZeroOrMorePathSegmentsMatcher that = (ZeroOrMorePathSegmentsMatcher) o;
            return pathPattern.equals(that.pathPattern);
        }
        @Override
        public int hashCode() {
            return Objects.hash(pathPattern);
        }
        @Override
        public String toString() {
            return "ZeroOrMorePathSegmentsMatcher{" + rawPattern + "}";
        }
    }

    static PatternMatcher parent(String pattern) {
        if(pattern.endsWith("?")) {
            throw new IllegalArgumentException(pattern);
        }else if(pattern.endsWith("*")) {
            throw new IllegalArgumentException(pattern);
        }else {
            return zeroOrMorePathSegmentMatcher(pattern, 0, PathPattern.RightHandPath.capturingZeroOrMoreSegments);
        }
    }

    static PatternMatcher child(String pattern) {
        final String doubleAsterisk = "/**";
        if(pattern.endsWith("?")) {
            if(hasPathVariable(pattern)) {
                throw new IllegalArgumentException(pattern);
            }else {
                return javaPatternMatcher(pattern, 1, ".");
            }
        }else if(pattern.endsWith(doubleAsterisk)) { // Double should come before single asterisk
            return zeroOrMorePathSegmentMatcher(pattern, doubleAsterisk.length(), PathPattern.RightHandPath.capturingZeroOrMoreSegments);
        }else if(pattern.endsWith("*")) {
            if(hasPathVariable(pattern)) {
                throw new IllegalArgumentException(pattern);
            }else {
                return javaPatternMatcher(pattern, 1, ".*[^/]");
            }
        }else {
            return zeroOrMorePathSegmentMatcher(pattern, 0, PathPattern.RightHandPath.capturingZeroSegments);
        }
    }

    private static boolean hasPathVariable(String pattern) {
        return pattern.indexOf('{') != -1;
    }

    private static JavaPatternMatcher javaPatternMatcher(
            String pattern, int suffixLengthToRemove, String patternToMatchSuffix) {
        final String prefix = Pattern.quote(pattern.substring(0, pattern.length() - suffixLengthToRemove));
        return new JavaPatternMatcher(pattern, Pattern.compile(prefix + patternToMatchSuffix));
    }

    private static PatternMatcher zeroOrMorePathSegmentMatcher(
            String pattern, int suffixLengthToRemove, PathPattern.RightHandPath rightHandPath) {
        final String prefix = pattern.substring(0, pattern.length() - suffixLengthToRemove);
        return new ZeroOrMorePathSegmentsMatcher(pattern, new PathPattern(prefix, rightHandPath));
    }
}
