package com.trvankiet.app.exception;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.exception.wrapper.PasswordStrongException;
import com.trvankiet.app.exception.wrapper.TokenException;
import com.trvankiet.app.exception.wrapper.UserNotEnabledException;
import com.trvankiet.app.exception.wrapper.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApiExceptionHandler {

    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    public <T extends BindException> ResponseEntity<GenericResponse> handleValidationException(final T e) {
        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleValidationException");
        final var badRequest = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                GenericResponse.builder()
                        .success(false)
                        .message(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage())
                        .result(null)
                        .statusCode(badRequest.value())
                        .build(), badRequest);
    }

    @ExceptionHandler(value = {
            UserNotFoundException.class,
            PasswordStrongException.class,
            UserNotEnabledException.class,
            TokenException.class
    })
    public <T extends RuntimeException> ResponseEntity<GenericResponse> handleApiRequestException(final T e) {
        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleApiRequestException");
        final var badRequest = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                GenericResponse.builder()
                        .success(false)
                        .message(e.getMessage())
                        .result(null)
                        .statusCode(badRequest.value())
                        .build(), badRequest);
    }

    @ExceptionHandler(value = {
            Exception.class,
            RuntimeException.class,
            IllegalArgumentException.class
    })
    public <T extends RuntimeException> ResponseEntity<GenericResponse> handleBasicException(final T e) {
            log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleBasicException");
            final var internalServerError = HttpStatus.BAD_REQUEST;

            return new ResponseEntity<>(
                    GenericResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .result(null)
                            .statusCode(internalServerError.value())
                            .build(), internalServerError);
    }
}
