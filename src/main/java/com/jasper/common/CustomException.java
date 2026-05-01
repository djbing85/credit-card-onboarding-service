package com.jasper.common;

import lombok.Getter;

/**
 * Custom business exception with error code, message and optional data
 */
@Getter
public class CustomException extends RuntimeException {
    
    private final Integer errorCode;
    private final String message;
    private final Object data;
    
    /**
     * Constructor with error code and message
     */
    public CustomException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.data = null;
    }
    
    /**
     * Constructor with error code, message and data
     */
    public CustomException(Integer errorCode, String message, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }
    
    /**
     * Constructor with error code, message, data and cause
     */
    public CustomException(Integer errorCode, String message, Object data, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }
    
    /**
     * Convenience constructor for duplicate entry (HTTP 409)
     */
    public static CustomException duplicateEntry(String message) {
        return new CustomException(409, message);
    }
    
    /**
     * Convenience constructor for duplicate entry with data (HTTP 409)
     */
    public static CustomException duplicateEntry(String message, Object data) {
        return new CustomException(409, message, data);
    }
    
    /**
     * Convenience constructor for bad request (HTTP 400)
     */
    public static CustomException badRequest(String message) {
        return new CustomException(400, message);
    }
    
    /**
     * Convenience constructor for not found (HTTP 404)
     */
    public static CustomException notFound(String message) {
        return new CustomException(404, message);
    }
    
    /**
     * Convenience constructor for internal server error (HTTP 500)
     */
    public static CustomException internalError(String message) {
        return new CustomException(500, message);
    }
}
