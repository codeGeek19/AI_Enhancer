package com.example.ai_image_enhancer.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "enhancement_requests")
public class EnhancementRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;

    private String contentType;

    private Long fileSize;

    private String status;

    private Long processingTimeMs;

    private LocalDateTime createdAt;

    public EnhancementRequest() {
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(
            String originalFileName) {

        this.originalFileName =
                originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(
            String contentType) {

        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(
            Long processingTimeMs) {

        this.processingTimeMs =
                processingTimeMs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }
}