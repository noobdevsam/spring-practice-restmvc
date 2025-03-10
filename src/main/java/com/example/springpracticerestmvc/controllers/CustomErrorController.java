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

@ControllerAdvice
public class CustomErrorController {

    @SuppressWarnings("rawtypes")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity handleBindErrors(MethodArgumentNotValidException exception) {

        List errorList = exception.getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return errorMap;
                }).collect(Collectors.toList());

        return ResponseEntity.badRequest().body(errorList);
    }

    @SuppressWarnings("rawtypes")
    @ExceptionHandler(TransactionSystemException.class)
    ResponseEntity handle_jpa_violations(TransactionSystemException exception) {
        ResponseEntity.BodyBuilder response_entity = ResponseEntity.badRequest();

        if (exception.getCause().getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception.getCause().getCause();

            List errors = cve.getConstraintViolations().stream()
                    .map(constraintViolation -> {
                        Map<String, String> error_map = new HashMap<>();
                        error_map.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
                        return error_map;
                    }).collect(Collectors.toList());

            return response_entity.body(errors);
        }

        return response_entity.build();
    }
}
