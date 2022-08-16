package com.fivefingers.boardrestapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorResult {
    private final String code;
    private final String message;

    @JsonInclude(Include.NON_EMPTY)
    private List<FieldNotValidError> fieldErrors;

    // @Valid Error가 없는 경우
    public static ErrorResult of(ErrorCode errorCode) {
        return new ErrorResult(errorCode.name(), errorCode.getMessage());
    }

    // @Valid Error가 있는 경우
    public static ErrorResult from(ErrorCode errorCode, List<FieldError> fieldErrors) {
        List<FieldNotValidError> list = fieldErrors.stream()
                .map(FieldNotValidError::of)
                .collect(Collectors.toList());
        return new ErrorResult(errorCode.name(), errorCode.getMessage(), list);
    }

    @Getter
    @RequiredArgsConstructor
    public static class FieldNotValidError {
        private final String field;
        private final String message;

        public static FieldNotValidError of(FieldError fieldError) {
            return new FieldNotValidError(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }

}
