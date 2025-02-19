package org.example.projectmanagementapi.service;

public interface AmazonSESService {
    void sendEmail(String to, String subject, String body);
}
