package com.trvankiet.app.exception;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.exception.wrapper.GroupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();
        Map<String, String> errors = new HashMap<>();
        objectErrors.forEach((error) ->{
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(
                GenericResponse.builder()
                        .success(false)
                        .message("Xảy ra lỗi khi xử lý dữ liệu!")
                        .result(errors)
                        .statusCode(badRequest.value())
                        .build(), badRequest);
    }

    @ExceptionHandler(value = {
            IllegalArgumentException.class
    })
    public <T extends RuntimeException> ResponseEntity<GenericResponse> handleIllegalArgumentException(final T e) {
        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleBasicException");
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
            GroupException.class
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
            NullPointerException.class,
            Exception.class,
            RuntimeException.class,
    })
    public <T extends RuntimeException> ResponseEntity<GenericResponse> handleServerException(final T e) {
        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleBasicException");
        final var internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(
                GenericResponse.builder()
                        .success(false)
                        .message(e.getMessage())
                        .result("Internal Server Error")
                        .statusCode(internalServerError.value())
                        .build(), internalServerError);
    }

    @ExceptionHandler(value = {
            MissingRequestHeaderException.class
    })
    public <T extends MissingRequestHeaderException> ResponseEntity<GenericResponse> handleMissingRequestHeaderException(final T e) {
        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleMissingRequestHeaderException");
        final var internalServerError = HttpStatus.UNAUTHORIZED;

        return new ResponseEntity<>(
                GenericResponse.builder()
                        .success(false)
                        .message("Access token is missing!")
                        .result("Unauthorized")
                        .statusCode(internalServerError.value())
                        .build(), internalServerError);
    }
}
