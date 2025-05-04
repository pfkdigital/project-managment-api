package org.example.projectmanagementapi.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.projectmanagementapi.dto.request.*;
import org.example.projectmanagementapi.dto.response.UserInformationDto;

public interface AuthService {
  void registerUser(RegisterRequestDto registerRequestDto);

  void verifyAccount(VerificationCodeDto verificationCodeDto);

  UserInformationDto loginUser(LoginRequestDto loginRequestDto, HttpServletResponse response);

  void forgotPassword(ForgotPasswordDto forgotPasswordDto);

  void resetPassword(String token, ResetPasswordRequestDto resetPasswordRequestDto);
}
