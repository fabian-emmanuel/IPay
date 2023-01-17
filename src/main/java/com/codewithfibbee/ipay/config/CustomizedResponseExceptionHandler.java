package com.codewithfibbee.ipay.config;

import com.codewithfibbee.ipay.exceptions.InvalidRequestException;
import com.codewithfibbee.ipay.exceptions.ProcessingException;
import com.codewithfibbee.ipay.exceptions.ResourceNotFoundException;
import com.codewithfibbee.ipay.util.ErrorResponse;
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
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(InvalidRequestException ex) {
        ErrorResponse errorMessage = new ErrorResponse(ex.getErrorMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProcessingException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ProcessingException ex) {
        ErrorResponse errorMessage = new ErrorResponse(ex.getErrorMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_MODIFIED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorMessage = new ErrorResponse(ex.getErrorMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_MODIFIED);
    }
}
