package org.example.projectmanagementapi.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.request.ForgotPasswordDto;
import org.example.projectmanagementapi.dto.request.LoginRequestDto;
import org.example.projectmanagementapi.dto.request.RegisterRequestDto;
import org.example.projectmanagementapi.dto.request.ResetPasswordRequestDto;
import org.example.projectmanagementapi.dto.response.UserInformationDto;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtUtility jwtUtility;
  private final CookieUtility cookieUtility;
  private final TokenUtility tokenUtility;

  @Override
  public void registerUser(RegisterRequestDto registerRequestDto) {

    if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
      throw new IllegalArgumentException("Email already exists");
    }

    if (userRepository.existsByUsername(registerRequestDto.getUsername())) {
      throw new IllegalArgumentException("Username already exists");
    }

    User newUser =
        User.builder()
            .email(registerRequestDto.getEmail())
            .username(registerRequestDto.getUsername())
            .password(registerRequestDto.getPassword())
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
  public UserInformationDto loginUser(LoginRequestDto loginRequestDto, HttpServletResponse response) {
    User user =
            userRepository
                    .findByUsername(loginRequestDto.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!user.isEnabled()) {
      throw new IllegalArgumentException("User is not verified");
    }

    if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Invalid credentials");
    }

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));

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
  public void resetPassword(String token, ResetPasswordRequestDto resetPasswordRequestDto) {
    PasswordResetToken passwordResetToken =
        passwordResetTokenRepository
            .findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Token not found"));

    User user = passwordResetToken.getUser();

    user.setPassword(resetPasswordRequestDto.getPassword());
    userRepository.save(user);

    passwordResetTokenRepository.delete(passwordResetToken);

    emailService.sendPasswordResetSuccessEmail(user.getEmail());
  }
}
