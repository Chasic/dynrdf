package com.dynrdf.webapp.exceptions;

/**
 * Exception type used when an item cannot be found.
 */
public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
