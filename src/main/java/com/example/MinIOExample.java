package com.example;

import io.minio.*;
import io.minio.messages.Item;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class MinIOExample {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String DOWNLOADS_DIR = "downloads";

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
                System.out.println("5. Abrir pasta de downloads");
                System.out.println("6. Remover objeto");
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
                        handleDownload(minioClient, bucketName);
                        break;
                    case 4:
                        listObjects(minioClient, bucketName);
                        break;
                    case 5:
                        openDownloadsFolder();
                        break;
                    case 6:
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

    private static void handleDownload(MinioClient minioClient, String bucketName) {
        try {
            System.out.print("\nNome do objeto (ex: photos/image.jpg): ");
            String objectName = scanner.nextLine().trim();

            if (objectName.isEmpty()) {
                System.out.println("Nome não pode ser vazio!");
                return;
            }

            // Pega só o nome do arquivo (ignora pastas)
            String fileName = objectName.substring(objectName.lastIndexOf('/') + 1);

            // Pergunta onde salvar
            System.out.print("Nome para salvar [" + fileName + "]: ");
            String customName = scanner.nextLine().trim();
            String finalName = customName.isEmpty() ? fileName : customName;

            // Executa o download
            boolean success = downloadFile(minioClient, bucketName, objectName, finalName);

            if (success) {
                System.out.println("✅ Download concluído: " + finalName);
            } else {
                System.out.println("❌ Falha no download");
            }

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static boolean downloadFile(MinioClient minioClient, String bucketName,
            String objectName, String saveAs) {
        try {
            // Cria pasta downloads se não existir
            Path downloadsPath = Paths.get(DOWNLOADS_DIR);
            if (!Files.exists(downloadsPath)) {
                Files.createDirectories(downloadsPath);
            }

            // Caminho completo do arquivo final
            Path destination = downloadsPath.resolve(saveAs);

            // Faz o download
            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build())) {

                Files.copy(stream, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            // Verifica se o arquivo foi criado
            return Files.exists(destination) && Files.size(destination) > 0;

        } catch (Exception e) {
            System.err.println("Erro no download: " + e.getMessage());
            return false;
        }
    }

    private static void openDownloadsFolder() {
        try {
            String downloadsPath = Paths.get(System.getProperty("user.dir"), DOWNLOADS_DIR).toString();

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("explorer", downloadsPath).start();
            } else {
                System.out.println("Pasta de downloads: " + downloadsPath);
            }
        } catch (Exception e) {
            System.err.println("Não foi possivel abrir a pasta de downloads" + e.getMessage());
        }
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