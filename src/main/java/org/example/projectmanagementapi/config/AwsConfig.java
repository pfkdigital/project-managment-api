package org.example.projectmanagementapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ses.SesClient;

import java.net.URI;

@Configuration
public class AwsConfig {

  @Value("${cloud.aws.credentials.accessKey}")
  private String accessKey;

  @Value("${cloud.aws.credentials.secretKey}")
  private String accessSecret;

  @Value("${cloud.aws.region.static}")
  private String region;

  @Value("${cloud.aws.endpoint.uri:#{null}}")
  private String endpointUrl;

  @Bean
  @Profile("!prod")
  public S3Client devS3Client() {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, accessSecret);
    return S3Client.builder()
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .endpointOverride(URI.create(endpointUrl))
        .region(Region.of(region))
        .forcePathStyle(true)
        .build();
  }

  @Bean
  @Profile("prod")
  public S3Client prodS3Client() {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, accessSecret);
    return S3Client.builder()
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .region(Region.of(region))
        .forcePathStyle(true)
        .build();
  }

  @Bean
  @Profile("!prod")
  public SesClient devSesClient() {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, accessSecret);
    return SesClient.builder()
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .endpointOverride(URI.create(endpointUrl))
        .region(Region.of(region))
        .build();
  }

  @Bean
  @Profile("prod")
  public SesClient prodSesClient() {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, accessSecret);
    return SesClient.builder()
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .region(Region.of(region))
        .build();
  }
}
