package org.example.projectmanagementapi.service;

public interface EmailService {
    void sendVerificationEmail(String to, String token);
    void sendAccountVerified(String to);
    void sendPasswordResetEmail(String to, String token);
    void sendPasswordResetSuccessEmail(String to);
}
