package com.dynrdf.webapp.exceptions;

/**
 * Exception type used in requests for an object
 */
public class RequestException extends Exception {
    public RequestException(String message) {
        super(message);
    }
}
