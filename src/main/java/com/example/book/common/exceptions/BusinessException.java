package com.example.book.common.exceptions;

//When something is wrong
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}
