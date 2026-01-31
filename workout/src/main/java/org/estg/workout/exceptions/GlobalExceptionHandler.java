package org.estg.workout.exceptions;

import jakarta.servlet.http.HttpServletRequest; // request info
import org.springframework.http.HttpStatus; // http status
import org.springframework.http.ResponseEntity; // response wrapper
import org.springframework.web.bind.MethodArgumentNotValidException; // validation exception
import org.springframework.web.bind.annotation.ExceptionHandler; // handler annotation
import org.springframework.web.bind.annotation.RestControllerAdvice; // global controller advice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException; // type mismatch (e.g., UUID)
import org.springframework.web.servlet.resource.NoResourceFoundException; // missing endpoint/static resource

import java.time.Instant; // timestamp

@RestControllerAdvice // applies to all controllers
public class GlobalExceptionHandler {

    // 404 not found handler (domain not found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {

        // build error body
        ApiError body = new ApiError(
                Instant.now(),                 // time
                HttpStatus.NOT_FOUND.value(),  // status code
                "Not Found",                   // status text
                ex.getMessage(),               // message
                req.getRequestURI()            // path
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body); // return 404
    }

    // 404 handler for missing endpoints / static resources (prevents fake 500s)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest req) {

        // build error body
        ApiError body = new ApiError(
                Instant.now(),                 // time
                HttpStatus.NOT_FOUND.value(),  // status code
                "Not Found",                   // status text
                ex.getMessage(),               // message
                req.getRequestURI()            // path
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body); // return 404
    }

    // 400 validation handler (bean validation / @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

        // pick first field error message
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("Validation error");

        // build error body
        ApiError body = new ApiError(
                Instant.now(),                    // time
                HttpStatus.BAD_REQUEST.value(),   // status code
                "Bad Request",                    // status text
                msg,                              // message
                req.getRequestURI()               // path
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body); // return 400
    }

    // 400 handler for invalid path/query param types (e.g., UUID parsing)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {

        // build error body
        ApiError body = new ApiError(
                Instant.now(),                    // time
                HttpStatus.BAD_REQUEST.value(),   // status code
                "Bad Request",                    // status text
                "Invalid identifier format",      // message
                req.getRequestURI()               // path
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body); // return 400
    }

    // 400 handler for our manual checks (member/trainer validation)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {

        // build error body
        ApiError body = new ApiError(
                Instant.now(),                    // time
                HttpStatus.BAD_REQUEST.value(),   // status code
                "Bad Request",                    // status text
                ex.getMessage(),                  // message
                req.getRequestURI()               // path
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body); // return 400
    }

    // fallback handler (unexpected errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {

        // build error body
        ApiError body = new ApiError(
                Instant.now(),                            // time
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // status code
                "Internal Server Error",                  // status text
                ex.getMessage(),                          // message
                req.getRequestURI()                       // path
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body); // return 500
    }
}
