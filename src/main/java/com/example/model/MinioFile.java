package com.example.model;

import java.time.ZonedDateTime;

public class MinioFile {
    private final String objectName;
    private final long size;
    private final ZonedDateTime lastModified;
    private final String contentType;

    public MinioFile(String objectName, long size, ZonedDateTime lastModified, String contentType) {
        this.objectName = objectName;
        this.size = size;
        this.lastModified = lastModified;
        this.contentType = contentType != null ? contentType : "application/octet-stream";
    }

    // Getters
    public String getObjectName() {
        return objectName;
    }

    public long getSize() {
        return size;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public String getContentType() {
        return contentType;
    }

    // Método auxiliar para exibição
    public String getFormattedSize() {
        if (size < 1024)
            return size + " B";
        if (size < 1024 * 1024)
            return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
}