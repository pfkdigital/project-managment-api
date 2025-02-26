package org.example.projectmanagementapi.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.projectmanagementapi.dto.request.ForgotPasswordDto;
import org.example.projectmanagementapi.dto.request.LoginRequestDto;
import org.example.projectmanagementapi.dto.request.RegisterRequestDto;
import org.example.projectmanagementapi.dto.request.ResetPasswordRequestDto;
import org.example.projectmanagementapi.dto.response.UserInformationDto;

public interface AuthService {
  void registerUser(RegisterRequestDto registerRequestDto);

  void verifyAccount(String token);

  UserInformationDto loginUser(LoginRequestDto loginRequestDto, HttpServletResponse response);

  void forgotPassword(ForgotPasswordDto forgotPasswordDto);

  void resetPassword(String token, ResetPasswordRequestDto resetPasswordRequestDto);
}
