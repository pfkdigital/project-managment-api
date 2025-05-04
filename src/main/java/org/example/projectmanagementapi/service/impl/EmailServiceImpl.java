package org.example.projectmanagementapi.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.service.AmazonSESService;
import org.example.projectmanagementapi.service.EmailService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
  private final AmazonSESService amazonSESService;

  private void sendEmail(String to, String subject, String templatePath, String token) {
    String body = readHtmlTemplate(templatePath);
    if (token != null) {
      body = body.replace("{{token}}", token);
    }
    amazonSESService.sendEmail(to, subject, body);
  }

  @Override
  public void sendVerificationEmail(String to, String token) {
    sendEmail(
        to,
        "Verify your email address",
        "src/main/resources/templates/verification-email.html",
        token);
  }

  @Override
  public void sendAccountVerified(String to) {
    sendEmail(
        to, "Account verified", "src/main/resources/templates/account-verified.html", null);
  }

  @Override
  public void sendPasswordResetEmail(String to, String token) {
    sendEmail(
        to,
        "Reset your password",
        "src/main/resources/templates/forgot-password.html",
        token);
  }

  @Override
  public void sendPasswordResetSuccessEmail(String to) {
    sendEmail(
        to,
        "Password reset successful",
        "src/main/resources/templates/reset-password-success-email.html",
        null);
  }

  private String readHtmlTemplate(String path) {
    String template = "";
    try {
      template = new String(Files.readAllBytes(Paths.get(path)));
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid template path");
    }

    return template;
  }
}
