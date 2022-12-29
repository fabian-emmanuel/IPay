package com.codewithfibbee.ipay.exceptions;

public class ProcessingException extends GenericRuntimeException {

    public ProcessingException(String message, Throwable error) {
        super(message, error);
    }
}
