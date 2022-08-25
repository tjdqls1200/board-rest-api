package com.fivefingers.boardrestapi.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class RestApiControllerAdvice extends ResponseEntityExceptionHandler {

    // 스프링 예외
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity
                .status(status)
                // DefaultHandlerExceptionResolver로 넘어가는 예외를 어떤식으로 처리할지
                // 묶어서 처리
                .body(new ErrorResult(status.toString(), ex.getMessage()));
    }

    //@Valid 유효성 통과하지 못하면 MethodArgumentNotValidException
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        CommonErrorCode errorCode = CommonErrorCode.VALIDATION_FAILED_PARAMETER;
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResult.from(errorCode, fieldErrors));
    }

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<ErrorResult> memberNotFoundExHandle(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResult errorResult = ErrorResult.of(errorCode);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResult);
    }
}
