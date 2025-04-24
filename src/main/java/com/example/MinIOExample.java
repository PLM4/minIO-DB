package com.example;

import io.minio.*;
import io.minio.errors.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinIOExample {
    public static void main(String[] args) {
        // Configuração do cliente
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build();

        String bucketName = "meu-bucket";
        String filePath = "man-playing-grassy-golf.jpg";
        String objectName = "man-playing-grassy-golf.jpg";
        String downloadPath = "./downloads/" + objectName.replace(".jpg", "-DOWNLOADED.jpg");

        try {
            // 1. Criar diretório de downloads se não existir
            new java.io.File("./downloads/").mkdirs();

            // 1. Criar bucket
            createBucket(minioClient, bucketName);

            // 2. Fazer upload
            uploadFile(minioClient, bucketName, objectName, filePath);

            // 3. Fazer download
            downloadFile(minioClient, bucketName, objectName, downloadPath);

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    public static void createBucket(MinioClient minioClient, String bucketName)
            throws Exception {
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

    public static void uploadFile(MinioClient minioClient, String bucketName,
            String objectName, String filePath) throws Exception {
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(filePath)
                        .build());
        System.out.println("Arquivo '" + objectName + "' carregado com sucesso.");
    }

    public static void downloadFile(MinioClient minioClient, String bucketName,
            String objectName, String downloadPath) throws Exception {
        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(downloadPath)
                        .build());
        System.out.println("Arquivo baixado para: " + downloadPath);
    }

    // // Listar objetos em um bucket
    // Iterable<Result<Item>> results = minioClient.listObjects(
    // ListObjectsArgs.builder().bucket(bucketName).build());

    // for (Result<Item> result : results) {
    // Item item = result.get();
    // System.out.println(item.objectName());
    // }

    // // Remover um objeto
    // minioClient.removeObject(
    // RemoveObjectArgs.builder()
    // .bucket(bucketName)
    // .object(objectName)
    // .build());
}