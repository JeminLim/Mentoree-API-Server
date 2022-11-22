package com.mentoree.exception;

import lombok.Getter;

@Getter
public class DuplicateDataException extends RuntimeException {

    private Class<?> entityClass;

    public DuplicateDataException() {
    }

    public DuplicateDataException(String message) {
        super(message);
    }

    public DuplicateDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateDataException(Class<?> entityClass, String message) {
        super(message);
        this.entityClass = entityClass;
    }

    public DuplicateDataException(Class<?> entityClass, String message, Throwable cause) {
        super(message, cause);
        this.entityClass = entityClass;
    }
}
