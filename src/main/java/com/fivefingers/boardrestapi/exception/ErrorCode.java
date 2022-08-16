package com.fivefingers.boardrestapi.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    // Enum 클래스에 선언된 메소드 (-> enum 상수의 이름 문자열 반환)
    String name();

    HttpStatus getHttpStatus();

    String getMessage();
}
