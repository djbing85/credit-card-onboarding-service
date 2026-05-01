package com.jasper.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    /**
     * Status code constants
     */
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_FAILURE = 500;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_BAD_REQUEST = 400;

    /**
     * 0 means success, 500 means failure
     */
    private Integer code;
    /**
     * error code, if success, it's null
     */
    private Integer errorCode;
    /**
     * error message, if success, it's null
     */
    private String message;
    /**
     * timestamp in ms
     */
    private Long ts;
    /**
     * data payload
     */
    private T data;

    /**
     * Create success response
     */
    public static <T> CommonResponse<T> success(T data) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setCode(CODE_SUCCESS);
        response.setData(data);
        response.setTs(System.currentTimeMillis());
        return response;
    }

    /**
     * Create success response without data
     */
    public static <T> CommonResponse<T> success() {
        return success(null);
    }

    /**
     * Create failure response
     */
    public static <T> CommonResponse<T> failure(Integer errorCode, String message) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setCode(CODE_FAILURE);
        response.setErrorCode(errorCode);
        response.setMessage(message);
        response.setTs(System.currentTimeMillis());
        return response;
    }

    /**
     * Create not found response
     */
    public static <T> CommonResponse<T> notFound(String message) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setCode(CODE_NOT_FOUND);
        response.setErrorCode(CODE_NOT_FOUND);
        response.setMessage(message);
        response.setTs(System.currentTimeMillis());
        return response;
    }
}
