package org.example.projectmanagementapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.service.AmazonS3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AmazonS3ServiceImpl implements AmazonS3Service {

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  private final S3Client s3Client;

  @Override
  public void uploadObject(MultipartFile file, String keyName) {
    try {
      RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
      PutObjectRequest request =
          PutObjectRequest.builder()
              .key(keyName)
              .bucket(bucketName)
              .contentType(file.getContentType())
              .contentLength(file.getSize())
              .build();

        s3Client.putObject(request, requestBody);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public DeleteObjectResponse deleteObject(String key) {
    try {
      DeleteObjectRequest request =
          DeleteObjectRequest.builder().key(key).bucket(bucketName).build();
      return s3Client.deleteObject(request);
    } catch (RuntimeException e) {
      throw new RuntimeException(e);
    }
  }
}
