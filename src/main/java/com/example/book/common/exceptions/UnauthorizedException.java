package com.example.book.common.exceptions;

//When user isnt authenticated
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

}
