package com.example.service;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class RemoveService {
    private final MinioClient minioClient;

    public RemoveService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void removeObject(String bucketName, String objectName)
            throws MinioException, IOException, InvalidKeyException, NoSuchAlgorithmException {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }
}
