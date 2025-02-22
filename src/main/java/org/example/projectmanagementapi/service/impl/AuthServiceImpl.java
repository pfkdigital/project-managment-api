package org.example.projectmanagementapi.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.*;
import org.example.projectmanagementapi.entity.PasswordResetToken;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.entity.VerificationToken;
import org.example.projectmanagementapi.enums.Role;
import org.example.projectmanagementapi.repository.PasswordResetTokenRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.repository.VerificationTokenRepository;
import org.example.projectmanagementapi.service.AuthService;
import org.example.projectmanagementapi.service.EmailService;
import org.example.projectmanagementapi.service.NotificationService;
import org.example.projectmanagementapi.util.CookieUtility;
import org.example.projectmanagementapi.util.JwtUtility;
import org.example.projectmanagementapi.util.TokenUtility;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final AuthenticationManager authenticationManager;
  private final EmailService emailService;
  private final NotificationService notificationService;
  private final UserRepository userRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final JwtUtility jwtUtility;
  private final CookieUtility cookieUtility;
  private final TokenUtility tokenUtility;

  @Override
  public void registerUser(RegisterDto registerDto) {
    if (userRepository.existsByEmail(registerDto.getEmail())) {
      throw new IllegalArgumentException("Email already exists");
    }

    User newUser =
        User.builder()
            .email(registerDto.getEmail())
            .username(registerDto.getUsername())
            .password(registerDto.getPassword())
            .role(Role.USER)
            .enabled(false)
            .build();

    User savedUser = userRepository.save(newUser);

    String verificationToken = tokenUtility.generateToken();
    VerificationToken newVerificationToken =
        VerificationToken.builder().token(verificationToken).user(savedUser).build();
    VerificationToken savedVerificationToken =
        verificationTokenRepository.save(newVerificationToken);

    emailService.sendVerificationEmail(savedUser.getEmail(), savedVerificationToken.getToken());
  }

  @Override
  public void verifyAccount(String token) {
    VerificationToken verificationToken =
        verificationTokenRepository
            .findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Token not found"));

    User derivedUser = verificationToken.getUser();

    derivedUser.setEnabled(true);
    User confirmedUser = userRepository.save(derivedUser);

    verificationTokenRepository.delete(verificationToken);

    emailService.sendAccountVerified(confirmedUser.getEmail());
  }

  @Override
  public UserInformationDto loginUser(LoginDto loginDto, HttpServletResponse response) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

    User user =
        userRepository
            .findByUsername(loginDto.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!user.isEnabled()) {
      throw new IllegalArgumentException("User is not verified");
    }

    if (!user.getPassword().equals(loginDto.getPassword())) {
      throw new IllegalArgumentException("Invalid credentials");
    }

    String accessToken = jwtUtility.generateAccessToken(user);
    String refreshToken = jwtUtility.generateRefreshToken(user);

    Cookie accessTokenCookie = cookieUtility.createTokenCookie("accessToken", accessToken);
    Cookie refreshTokenCookie = cookieUtility.createTokenCookie("refreshToken", refreshToken);

    response.addCookie(accessTokenCookie);
    response.addCookie(refreshTokenCookie);

    return UserInformationDto.builder()
        .username(user.getUsername())
        .email(user.getEmail())
        .role(user.getRole())
        .displayImageUrl(user.getDisplayImageUrl())
        .build();
  }

  @Override
  public void forgotPassword(ForgotPasswordDto forgotPasswordDto) {
    User user =
        userRepository
            .findByEmail(forgotPasswordDto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    String resetToken = tokenUtility.generateToken();
    PasswordResetToken newPasswordResetToken =
        PasswordResetToken.builder().token(resetToken).user(user).build();
    PasswordResetToken savedToken = passwordResetTokenRepository.save(newPasswordResetToken);

    emailService.sendPasswordResetEmail(user.getEmail(), savedToken.getToken());
  }

  @Override
  public void resetPassword(String token, ResetPasswordDto resetPasswordDto) {
    PasswordResetToken passwordResetToken =
        passwordResetTokenRepository
            .findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Token not found"));

    User user = passwordResetToken.getUser();

    user.setPassword(resetPasswordDto.getPassword());
    userRepository.save(user);

    passwordResetTokenRepository.delete(passwordResetToken);

    emailService.sendPasswordResetSuccessEmail(user.getEmail());
  }
}
