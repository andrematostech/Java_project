package org.estg.schedule.controller;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.estg.schedule.exceptions.SessionConflictException;
import org.estg.schedule.exceptions.SessionNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSessionNotFound(SessionNotFoundException ex, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", 404);
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(SessionConflictException.class)
    public ResponseEntity<Map<String, Object>> handleSessionConflict(SessionConflictException ex, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", 409);
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", 409);
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
