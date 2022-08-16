package com.fivefingers.boardrestapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    VALIDATION_FAILED_PARAMETER(HttpStatus.BAD_REQUEST, "Not Valid Parameter");

    private final HttpStatus httpStatus;
    private final String message;
}
