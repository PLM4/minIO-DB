package com.example.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.stereotype.Service;

@Service
public class BucketService {
    
    private final MinioClient minioClient;
    
    public BucketService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }
    
    public void createBucket(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            System.out.println("Bucket '" + bucketName + "' criado com sucesso.");
        } else {
            System.out.println("Bucket '" + bucketName + "' já existe.");
        }
    }
}