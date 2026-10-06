package com.example.ai_image_enhancer.dto;

public record EnhancementResponse(
        Long requestId,
        String status,
        String enhancedImageUrl,
        Long processingTimeMs
) {
}
