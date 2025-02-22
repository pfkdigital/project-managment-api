package org.example.projectmanagementapi.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.ForgotPasswordDto;
import org.example.projectmanagementapi.dto.LoginDto;
import org.example.projectmanagementapi.dto.RegisterDto;
import org.example.projectmanagementapi.dto.ResetPasswordDto;
import org.example.projectmanagementapi.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
    authService.registerUser(registerDto);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/verify")
  public ResponseEntity<?> verifyAccount(@RequestParam String token) {
    authService.verifyAccount(token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto, HttpServletResponse response) {
    return new ResponseEntity<>(authService.loginUser(loginDto, response), HttpStatus.OK);
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
    authService.forgotPassword(forgotPasswordDto);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(
      @RequestParam String token, @RequestBody ResetPasswordDto resetPasswordDto) {
    authService.resetPassword(token, resetPasswordDto);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
