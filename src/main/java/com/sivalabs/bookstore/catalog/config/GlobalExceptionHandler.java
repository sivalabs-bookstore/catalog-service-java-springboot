package com.sivalabs.bookstore.catalog.config;

import jakarta.validation.ConstraintViolation;
import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String errorMessage =
                ex.getBindingResult().getAllErrors().stream()
                        .map(err -> err.unwrap(ConstraintViolation.class))
                        .map(
                                err ->
                                        String.format(
                                                "'%s' %s", err.getPropertyPath(), err.getMessage()))
                        .collect(Collectors.joining(","));
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
        problemDetail.setTitle("Invalid Request");
        problemDetail.setType(URI.create("https://api.bookmarks.com/errors/bad-request"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorCategory", "Validation");

        return ResponseEntity.badRequest().body(problemDetail);
    }
}
