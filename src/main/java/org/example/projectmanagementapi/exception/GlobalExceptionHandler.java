package org.example.projectmanagementapi.exception;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
    ApiError apiError =
        ApiError.builder()
            .message(ex.getMessage())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .timestamp(LocalDateTime.now())
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
    ApiError apiError =
        ApiError.builder()
            .message(ex.getMessage())
            .status(HttpStatus.BAD_REQUEST.value())
            .timestamp(LocalDateTime.now())
            .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleException(Exception ex) {
    ApiError apiError =
        ApiError.builder()
            .message("An unexpected error occurred")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .timestamp(LocalDateTime.now())
            .build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
  }
}
