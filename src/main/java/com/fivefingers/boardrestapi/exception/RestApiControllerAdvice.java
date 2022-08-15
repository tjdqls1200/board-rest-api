package com.fivefingers.boardrestapi.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestApiControllerAdvice extends ResponseEntityExceptionHandler {

    //@Valid Exception - MethodArgumentNotValidException
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResult("Not Valid", ex.getBindingResult().toString()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MemberNotFoundException.class)
    public ErrorResult memberNotFoundExHandle(MemberNotFoundException e) {
        return new ErrorResult("Not Found", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateMemberException.class)
    public ErrorResult duplicateMemberExHanlde(DuplicateMemberException e) {
        return new ErrorResult("Duplicate", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoginNotEqualsException.class)
    public ErrorResult LoginNotEqualsExHandle(LoginNotEqualsException e) {
        return new ErrorResult("NotEquals", e.getMessage());
    }
}
