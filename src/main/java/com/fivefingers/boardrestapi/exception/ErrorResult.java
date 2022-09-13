package com.fivefingers.boardrestapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorResult {
    private final String code;
    private final String message;

    @JsonInclude(Include.NON_EMPTY)
    private List<NotValidFieldError> fieldErrors;

    // @Valid Error가 없는 경우
    public static ErrorResult of(ErrorCode errorCode) {
        return new ErrorResult(errorCode.name(), errorCode.getMessage());
    }

    // @Valid Error가 있는 경우
    public static ErrorResult from(ErrorCode errorCode, List<FieldError> fieldErrors) {
        List<NotValidFieldError> list = fieldErrors.stream()
                .map(NotValidFieldError::from)
                .collect(Collectors.toList());

        return new ErrorResult(errorCode.name(), errorCode.getMessage(), list);
    }

    @Getter
    @RequiredArgsConstructor
    public static class NotValidFieldError {
        private final String field;
        private final String message;

        public static NotValidFieldError from(FieldError fieldError) {
            return new NotValidFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }

}
