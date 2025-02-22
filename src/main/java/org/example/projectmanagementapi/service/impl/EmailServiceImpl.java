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

  @Override
  public void sendVerificationEmail(String to, String token) {
    String subject = "Verify your email address";
    String body = readHtmlTemplate("src/main/resources/templates/verification-email.html");
    body = body.replace("{{token}}", token);
    amazonSESService.sendEmail(to, subject, body);
  }

  @Override
  public void sendAccountVerified(String to) {
    String subject = "Account verified";
    String body = readHtmlTemplate("src/main/resources/templates/account-verified-email.html");
    amazonSESService.sendEmail(to, subject, body);
  }

  @Override
  public void sendPasswordResetEmail(String to, String token) {
    String subject = "Reset your password";
    String body = readHtmlTemplate("src/main/resources/templates/forgot-password-email.html");
    body = body.replace("{{token}}", token);
    amazonSESService.sendEmail(to, subject, body);
  }

  @Override
  public void sendPasswordResetSuccessEmail(String to) {
    String subject = "Password reset successful";
    String body =
        readHtmlTemplate("src/main/resources/templates/reset-password-success-email.html");
    amazonSESService.sendEmail(to, subject, body);
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
