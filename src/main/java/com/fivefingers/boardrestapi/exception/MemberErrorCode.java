package com.fivefingers.boardrestapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    DUPLICATE_LOGIN_ID(HttpStatus.BAD_REQUEST, "Duplicate loginId"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Member Not Found");

    private final HttpStatus httpStatus;
    private final String message;
}
