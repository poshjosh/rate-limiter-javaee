package io.github.poshjosh.ratelimiter.web.javaee.uri.wip;

final class UriParseException extends RuntimeException{
    UriParseException() { }
    UriParseException(String errorText, int errorPosition, Throwable cause) {
        super(constructMessage(errorText, errorPosition), cause);
    }
    UriParseException(String errorText, char errorChar, int errorPosition) {
        this(constructMessage(errorText, errorPosition));
    }
    private static String constructMessage(String errorText, int errorPosition) {
        return "Invalid text at position: " + errorPosition + ", text: " + errorText;
    }

    public UriParseException(String message) {
        super(message);
    }

    private static String constructMessage(String errorText, char errorChar, int errorPosition) {
        return "Invalid character: " + errorChar + " at position: " + errorPosition + " of text: " + errorText;
    }
}
