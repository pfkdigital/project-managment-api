package org.example.projectmanagementapi.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.annotation.RateLimitedRestController;
import org.example.projectmanagementapi.dto.request.*;
import org.example.projectmanagementapi.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RateLimitedRestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto registerRequestDto) {
    authService.registerUser(registerRequestDto);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/verify")
  public ResponseEntity<?> verifyAccount(@RequestBody VerificationCodeDto verificationCodeDto) {
    authService.verifyAccount(verificationCodeDto);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
    return new ResponseEntity<>(authService.loginUser(loginRequestDto, response), HttpStatus.OK);
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
    authService.forgotPassword(forgotPasswordDto);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(
      @RequestParam String token, @RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
    authService.resetPassword(token, resetPasswordRequestDto);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
