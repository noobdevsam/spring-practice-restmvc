package com.example.springpracticerestmvc.controllers;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CustomErrorController is a global exception handler for the application.
 * It provides methods to handle specific exceptions and return appropriate HTTP responses.
 */
@ControllerAdvice
public class CustomErrorController {

    /**
     * Handles validation errors caused by invalid method arguments.
     *
     * @param exception The MethodArgumentNotValidException containing validation errors.
     * @return A ResponseEntity with a list of field errors and HTTP status BAD_REQUEST.
     */
    @SuppressWarnings("rawtypes")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity handleBindErrors(MethodArgumentNotValidException exception) {

        // Extract field errors and map them to a list of error messages
        List errorList = exception.getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return errorMap;
                }).collect(Collectors.toList());

        // Return the error list with HTTP status BAD_REQUEST
        return ResponseEntity.badRequest().body(errorList);
    }

    /**
     * Handles JPA transaction violations, specifically ConstraintViolationException.
     *
     * @param exception The TransactionSystemException containing the cause of the violation.
     * @return A ResponseEntity with a list of constraint violations and HTTP status BAD_REQUEST.
     */
    @SuppressWarnings("rawtypes")
    @ExceptionHandler(TransactionSystemException.class)
    ResponseEntity handle_jpa_violations(TransactionSystemException exception) {
        ResponseEntity.BodyBuilder response_entity = ResponseEntity.badRequest();

        // Check if the cause of the exception is a ConstraintViolationException
        if (exception.getCause().getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception.getCause().getCause();

            // Extract constraint violations and map them to a list of error messages
            List errors = cve.getConstraintViolations().stream()
                    .map(constraintViolation -> {
                        Map<String, String> error_map = new HashMap<>();
                        error_map.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
                        return error_map;
                    }).collect(Collectors.toList());

            // Return the error list with HTTP status BAD_REQUEST
            return response_entity.body(errors);
        }

        // Return an empty response with HTTP status BAD_REQUEST if no violations are found
        return response_entity.build();
    }
}