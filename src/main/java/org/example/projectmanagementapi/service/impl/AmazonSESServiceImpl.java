package org.example.projectmanagementapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.service.AmazonSESService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@RequiredArgsConstructor
public class AmazonSESServiceImpl implements AmazonSESService {
  private final SesClient sesClient;

  @Override
  public void sendEmail(String to, String subject, String body) {
    try {
      Destination destination = Destination.builder().toAddresses(to).build();

      Content subjectContent = Content.builder().data(subject).build();
      Content htmlContent = Content.builder().data(body).build();
      Body bodyContent = Body.builder().html(htmlContent).build();

      Message message = Message.builder().subject(subjectContent).body(bodyContent).build();
      SendEmailRequest request =
          SendEmailRequest.builder().destination(destination).message(message).build();

      sesClient.sendEmail(request);
    } catch (AwsServiceException | SdkClientException | UnsupportedOperationException e) {
      throw new RuntimeException("Failed to send email", e);
    }
  }
}
