package org.estg.trainers.controller;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.estg.trainers.exceptions.DuplicateTrainerException;
import org.estg.trainers.exceptions.ScheduleConflictException;
import org.estg.trainers.exceptions.TrainerNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TrainerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTrainerNotFound(TrainerNotFoundException ex,
                                                                     HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(DuplicateTrainerException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateTrainer(DuplicateTrainerException ex,
                                                                      HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<Map<String, Object>> handleScheduleConflict(ScheduleConflictException ex,
                                                                      HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                HttpServletRequest req) {
        Map<String, Object> body = base(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI());

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        body.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBadJson(HttpMessageNotReadableException ex,
                                                             HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request body", req.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                   HttpServletRequest req) {
        // Commonly: unique constraint on email/phone, not-null, FK violations, etc.
        log.warn("Data integrity violation for {} {}: {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.CONFLICT, "Data integrity violation", req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception for {} {}:", req.getMethod(), req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", req.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message, String path) {
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(status).body(base(status, message, path));
    }

    private Map<String, Object> base(HttpStatus status, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return body;
    }
}
