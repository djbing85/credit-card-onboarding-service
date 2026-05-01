package com.jasper.config;

import com.jasper.common.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Global response wrapper advice
 * Sets HTTP status code based on CommonResponse code
 */
@RestControllerAdvice
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Only process CommonResponse
        return CommonResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // Set HTTP status code based on CommonResponse code
        if (body instanceof CommonResponse) {
            CommonResponse<?> commonResponse = (CommonResponse<?>) body;
            
            if (commonResponse.getCode() == CommonResponse.CODE_SUCCESS) {
                response.setStatusCode(org.springframework.http.HttpStatus.OK);
            } else if (commonResponse.getCode() == CommonResponse.CODE_NOT_FOUND) {
                response.setStatusCode(org.springframework.http.HttpStatus.NOT_FOUND);
            } else {
                response.setStatusCode(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        
        return body;
    }
}
