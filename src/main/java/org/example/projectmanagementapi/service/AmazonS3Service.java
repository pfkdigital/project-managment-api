package org.example.projectmanagementapi.service;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

public interface AmazonS3Service {
    void uploadObject(MultipartFile file, String keyName);
    DeleteObjectResponse deleteObject(String key);
}
