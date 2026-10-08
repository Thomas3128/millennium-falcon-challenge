package com.falcon.falcon.routes;

/**
 * Thrown when the routes database cannot be read. Wraps the underlying JDBC
 * failure so that callers never need to know the routes come from SQL.
 */
public class RoutesReaderException extends RuntimeException {

    public RoutesReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
