package org.rotary.exchange.backend.exception;

public class AccessDeniedException extends RuntimeException {
    
    public AccessDeniedException(String message) {
        super(message);
    }
    
    public AccessDeniedException(String resourceName, Integer resourceId) {
        super("You do not have permission to access " + resourceName + " with id " + resourceId);
    }
}
