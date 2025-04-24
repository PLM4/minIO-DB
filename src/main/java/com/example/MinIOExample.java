package com.example;

import io.minio.*;
import io.minio.messages.Item;
import io.minio.errors.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class MinIOExample {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Configuração do cliente
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build();

        String bucketName = "meu-bucket";

        try {
            int opcao;
            do {
                System.out.println("\n=== Menu MinIO ===");
                System.out.println("1. Criar bucket");
                System.out.println("2. Fazer upload de arquivo");
                System.out.println("3. Fazer download de arquivo");
                System.out.println("4. Listar objetos no bucket");
                System.out.println("5. Remover objeto");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");

                opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar buffer

                switch (opcao) {
                    case 1:
                        createBucket(minioClient, bucketName);
                        break;
                    case 2:
                        System.out.print("Digite o caminho do arquivo para upload: ");
                        String filePath = scanner.nextLine();
                        System.out.print("Digite o nome do objeto no bucket: ");
                        String objectName = scanner.nextLine();
                        uploadFile(minioClient, bucketName, objectName, filePath);
                        break;
                    case 3:
                        System.out.print("Digite o nome do objeto para download: ");
                        String downloadObject = scanner.nextLine();
                        System.out.print("Digite o caminho para salvar o download: ");
                        String downloadPath = scanner.nextLine();
                        downloadFile(minioClient, bucketName, downloadObject, downloadPath);
                        break;
                    case 4:
                        listObjects(minioClient, bucketName);
                        break;
                    case 5:
                        System.out.print("Digite o nome do objeto para remover: ");
                        String objectToRemove = scanner.nextLine();
                        removeObject(minioClient, bucketName, objectToRemove);
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } while (opcao != 0);

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
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

    public static void listObjects(MinioClient minioClient, String bucketName)
            throws Exception {
        System.out.println("\nListando objetos no bucket '" + bucketName + "':");

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build());

        int count = 0;
        for (Result<Item> result : results) {
            Item item = result.get();
            System.out.println(++count + ". " + item.objectName() +
                    " (Tamanho: " + item.size() + " bytes)");
        }

        if (count == 0) {
            System.out.println("O bucket está vazio.");
        }
    }

    public static void removeObject(MinioClient minioClient, String bucketName,
            String objectName) throws Exception {
        System.out.println("\nRemovendo objeto '" + objectName + "'...");

        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());

        System.out.println("Objeto removido com sucesso!");
    }
}