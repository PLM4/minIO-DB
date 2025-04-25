package com.example.service;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

import java.util.Scanner;

@Service
public class DownloadService {

    private final MinioClient minioClient;
    private final Scanner scanner;

    @Value("${downloads.dir}")
    private String downloadsDir;

    public DownloadService(MinioClient minioClient) {
        this.minioClient = minioClient;
        this.scanner = new Scanner(System.in);
    }

    public boolean downloadFile(String bucketName, String objectName, String saveAs) throws Exception {
        try {
            Path destination = Paths.get(downloadsDir, saveAs);
            Files.createDirectories(destination.getParent());

            try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build())) {
                Files.copy(stream, destination, StandardCopyOption.REPLACE_EXISTING);

                if (!Files.exists(destination) || Files.size(destination) == 0) {
                    throw new IOException("Arquivo vazio ou não criado");
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("Erro durante o download: " + e.getMessage());
            return false;
        }
    }

    public void handleDownload(String bucketName) throws Exception {
        System.out.println("\nObjetos disponiveis no bucket '" + bucketName + "':");

        System.out.print("Digite o nome do objeto para download: ");
        String objectName = scanner.nextLine();

        System.out.print("Digite o nome para salvar [Enter para nome original]: ");
        String saveAs = scanner.nextLine();

        if (saveAs.isBlank()) {
            saveAs = objectName.contains("/") ? objectName.substring(objectName.lastIndexOf("/") + 1) : objectName;
        }

        boolean success = downloadFile(bucketName, objectName, saveAs);

        if (success) {
            System.out.println("\n✅ Download concluído com sucesso!");
            System.out.println("📍 Local: " + Paths.get(downloadsDir, saveAs).toAbsolutePath());
        } else {
            System.out.println("❌ Falha no download");
        }
    }

    public void openDownloadsFolder() {
        try {
            Path downloadsPath = Paths.get(downloadsDir).toAbsolutePath();
            if (!Files.exists(downloadsPath)) {
                Files.createDirectories(downloadsPath);
            }

            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("explorer", downloadsPath.toString()).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", downloadsPath.toString()).start();
            } else {
                System.out.println("📁 Pasta de downloads: " + downloadsPath);
            }

        } catch (Exception e) {
            System.err.println("Não foi possivel abrir a pasta: " + e.getMessage());
        }
    }
}
