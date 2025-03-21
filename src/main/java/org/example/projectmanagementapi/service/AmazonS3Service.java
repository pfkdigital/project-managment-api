package org.example.projectmanagementapi.service;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public interface AmazonS3Service {
    PutObjectResponse uploadObject(MultipartFile file,String keyName);
    DeleteObjectResponse deleteObject(String key);
}
