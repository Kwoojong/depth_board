package com.hello.borad.application.presentation;

import com.hello.borad.dto.ErrorResponse;
import com.hello.borad.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalCommonControllerAdvice {

    private static final String LOG_FORMAT = "Error={}";
    private static final String INVALID_REQUEST_MESSAGE = "잘못된 요청입니다.";

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> customException(CustomException e) {
        int statusCode = e.getStatusCode();

        ErrorResponse body = ErrorResponse.builder()
                .code(statusCode)
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();

        log.info(LOG_FORMAT, body, e);
        return ResponseEntity.status(statusCode).body(body);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({
            BindException.class,
            MethodArgumentNotValidException.class
    })
    public ErrorResponse invalidRequestHandler(BindException e) {
        ErrorResponse body = ErrorResponse.builder()
                .code(BAD_REQUEST.value())
                .message(INVALID_REQUEST_MESSAGE)
                .build();

        e.getFieldErrors().forEach((fieldError) ->
                body.addValidation(fieldError.getField(), fieldError.getDefaultMessage()));

        log.info(LOG_FORMAT, body, e);
        return body;
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleInternalServerError(RuntimeException e) {
        ErrorResponse body = ErrorResponse.builder()
                .code(INTERNAL_SERVER_ERROR.value())
                .message(e.getMessage())
                .build();

        log.error("RuntimeException ", e);
        return body;
    }
}
