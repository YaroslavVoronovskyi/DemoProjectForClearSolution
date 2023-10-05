package com.gmail.voronovskyi.yaroslav.demo.controller.exception;

import lombok.AllArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.InternalServerErrorException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
public class CarRestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getField() + " " + x.getDefaultMessage())
                .collect(Collectors.toList());
        ApiError apiError = ApiError.builder()
                .error(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .message(errors)
                .build();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handlerRequestException(EntityNotFoundException exception) {
        ApiError apiError = ApiError.builder()
                .error(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .message(List.of(exception.getMessage()))
                .build();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(value = {EmptyResultDataAccessException.class})
    public ResponseEntity<Object> handlerRequestException(EmptyResultDataAccessException exception) {
        ApiError apiError = ApiError.builder()
                .error(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .message(List.of(Objects.requireNonNull(exception.getMessage())))
                .build();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handlerRequestException(IllegalArgumentException exception) {
        ApiError apiError = ApiError.builder()
                .error(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .message(List.of(exception.getMessage()))
                .build();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(value = {NotValidAgeException.class})
    public ResponseEntity<Object> handlerRequestException(NotValidAgeException exception) {
        ApiError apiError = ApiError.builder()
                .error(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .message(List.of(exception.getMessage()))
                .build();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(value = {InternalServerErrorException.class})
    public ResponseEntity<Object> handlerRequestException(InternalServerErrorException exception) {
        ApiError apiError = ApiError.builder()
                .error(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now())
                .message(List.of(exception.getMessage()))
                .build();
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
