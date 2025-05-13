package org.example.projectmanagementapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitationException extends RuntimeException {
  public RateLimitationException(String message) {
    super(message);
  }

  public ApiError toApiErrorMessage() {
    return ApiError.builder()
        .status(HttpStatus.TOO_MANY_REQUESTS.value())
        .message(getMessage())
        .build();
  }
}
