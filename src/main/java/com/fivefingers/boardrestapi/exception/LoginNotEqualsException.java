package com.fivefingers.boardrestapi.exception;

public class LoginNotEqualsException extends RuntimeException {
    public LoginNotEqualsException(String message) {
        super(message);
    }
}
