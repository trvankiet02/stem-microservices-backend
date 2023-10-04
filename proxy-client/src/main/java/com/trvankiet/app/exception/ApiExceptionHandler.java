package com.trvankiet.app.exception;

import com.trvankiet.app.constant.GenericResponse;
import com.trvankiet.app.exception.wrapper.MyFeignException;
import feign.FeignException;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse>, handleValidationException");
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
            MyFeignException.class
    })
    public <T extends RuntimeException> ResponseEntity<GenericResponse> handleApiRequestException(final T e) {

        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse>, handleApiRequestException");
        final var badRequest = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                GenericResponse.builder()
                        .success(false)
                        .message(e.getMessage())
                        .result(null)
                        .statusCode(badRequest.value())
                        .build(), badRequest);
    }

}
