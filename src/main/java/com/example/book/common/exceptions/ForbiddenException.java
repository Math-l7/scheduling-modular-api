package com.example.book.common.exceptions;

//When not ahtorizated
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

}
