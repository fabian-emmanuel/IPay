package com.codewithfibbee.ipay.config;

import com.codewithfibbee.ipay.exceptions.InvalidRequestException;
import com.codewithfibbee.ipay.exceptions.ProcessingException;
import com.codewithfibbee.ipay.exceptions.ResourceNotFoundException;
import com.codewithfibbee.ipay.util.ApiResponseUtil;
import com.codewithfibbee.ipay.util.ErrorResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class CustomizedResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponseEntity> handleResourceNotFoundException(InvalidRequestException e) {
        return ApiResponseUtil.errorResponse(HttpStatus.BAD_REQUEST, e.getErrorMessage());
    }

    @ExceptionHandler(ProcessingException.class)
    public ResponseEntity<ErrorResponseEntity> handleResourceNotFoundException(ProcessingException e) {
        return ApiResponseUtil.errorResponse(HttpStatus.NOT_MODIFIED, e.getErrorMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseEntity> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ApiResponseUtil.errorResponse(HttpStatus.NOT_MODIFIED, e.getErrorMessage());
    }
}
