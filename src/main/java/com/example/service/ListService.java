package com.example.service;

import com.example.model.MinioFile;
import io.minio.*;
import io.minio.messages.Item;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ListService {
    private final MinioClient minioClient;

    public ListService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public List<MinioFile> listObjects(String bucketName) throws Exception {
        List<MinioFile> files = new ArrayList<>();

        // Lista os objetos no bucket
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .build());

        for (Result<Item> result : results) {
            Item item = result.get();
            String objectName = item.objectName();

            // Obtém metadados completos do objeto
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());

            files.add(new MinioFile(
                    item.objectName(),
                    item.size(),
                    item.lastModified(),
                    stat.contentType()));

        }
        return files;
    }
}