package org.rotary.exchange.backend.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String field;
    private final Integer fieldId;
    private final String fieldValue;

    public ResourceNotFoundException(String resourceName, String field, Integer fieldId) {
        super(resourceName + " with " + field + " " + fieldId + " not found");
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
        this.fieldValue = null;
    }

    public ResourceNotFoundException(String resourceName, String field, String fieldValue) {
        super(resourceName + " with " + field + " " + fieldValue + " not found");
        this.resourceName = resourceName;
        this.field = field;
        this.fieldValue = fieldValue;
        this.fieldId = null;
    }
}