package com.example.controller;

import com.example.service.*;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
public class MinioController {

    private final BucketService bucketService;
    private final DownloadService downloadService;
    private final ListService listService;
    private final RemoveService removeService;
    private final UploadService uploadService;
    private final Scanner scanner;
    private String bucketCorrente = "meu-bucket";

    public MinioController(BucketService bucketService,
            DownloadService downloadService,
            ListService listService,
            RemoveService removeService,
            UploadService uploadService) {
        this.bucketService = bucketService;
        this.uploadService = uploadService;
        this.downloadService = downloadService;
        this.removeService = removeService;
        this.listService = listService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            int opcao;
            do {
                exibirMenu();
                opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar buffer

                switch (opcao) {
                    case 1 -> criarBucket();
                    case 2 -> fazerUpload();
                    case 3 -> fazerDownload();
                    case 4 -> listarObjetos();
                    case 5 -> abrirPastaDownloads();
                    case 6 -> removerObjeto();
                    case 0 -> System.out.println("Saindo...");
                    default -> System.out.println("Opção inválida!");
                }
            } while (opcao != 0);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private void exibirMenu() {
        System.out.println("\n=== Menu MinIO ===");
        System.out.println("1. Criar bucket");
        System.out.println("2. Fazer upload de arquivo");
        System.out.println("3. Fazer download de arquivo");
        System.out.println("4. Listar objetos no bucket");
        System.out.println("5. Abrir pasta de downloads");
        System.out.println("6. Remover objeto");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private void criarBucket() throws Exception {
        System.out.print("Digite o nome do bucket: ");
        String bucketName = scanner.nextLine();
        bucketService.createBucket(bucketName);
        bucketCorrente = bucketName;
    }

    private void fazerUpload() throws Exception {
        System.out.print("Digite o caminho completo do arquivo: ");
        String filePath = scanner.nextLine();
        System.out.print("Digite o nome para o objeto no bucket [" + bucketCorrente + "]: ");
        String objectName = scanner.nextLine();
        uploadService.uploadFile(bucketCorrente, objectName, filePath);
    }

    private void fazerDownload() throws Exception {
        System.out.println("\n=== Download de Arquivo ===");
        downloadService.handleDownload(bucketCorrente);
    }

    private void listarObjetos() throws Exception {
        System.out.println("\nObjetos no bucket '" + bucketCorrente + "':");
        listService.listObjects(bucketCorrente)
                .forEach(obj -> System.out.println("-" + obj.getObjectName() + " (" + obj.getFormattedSize() + ")"));
    }

    private void abrirPastaDownloads() {
        downloadService.openDownloadsFolder();
    }

    private void removerObjeto() throws Exception {
        System.out.println("Objetos disponíveis no bucket '" + bucketCorrente + "':");

        listService.listObjects(bucketCorrente)
                .forEach(obj -> System.out.println(obj.getObjectName() + " (" + obj.getFormattedSize() + ")"));

        System.out.print("Digite o nome do objeto para remover: ");
        String objectName = scanner.nextLine();
        removeService.removeObject(bucketCorrente, objectName);
    }
}
