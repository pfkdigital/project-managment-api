package org.example.projectmanagementapi.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.projectmanagementapi.dto.*;

public interface AuthService {
  void registerUser(RegisterDto registerDto);

  void verifyAccount(String token);

  UserInformationDto loginUser(LoginDto loginDto, HttpServletResponse response);

  void forgotPassword(ForgotPasswordDto forgotPasswordDto);

  void resetPassword(String token, ResetPasswordDto resetPasswordDto);
}
