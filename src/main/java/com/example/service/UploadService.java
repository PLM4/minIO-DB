package com.example.service;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.UploadObjectArgs;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UploadService {

    private final MinioClient minioClient;

    public UploadService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void uploadFile(String bucketName, String objectName, String filePath)
            throws MinioException, IOException, InvalidKeyException, NoSuchAlgorithmException {

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("Arquivo não encontrado." + filePath);
        }

        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(filePath)
                        .build());
    }
}
