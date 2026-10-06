package com.example.ai_image_enhancer.service;

import com.example.ai_image_enhancer.client.ReplicateClient;
import com.example.ai_image_enhancer.dto.EnhancementResponse;
import com.example.ai_image_enhancer.entity.EnhancementRequest;
import com.example.ai_image_enhancer.exception.AIServiceException;
import com.example.ai_image_enhancer.exception.InvalidImageException;
import com.example.ai_image_enhancer.repository.EnhancementRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class ImageEnhancerService {

    private final ReplicateClient replicateClient;

    private final EnhancementRequestRepository repository;


    public ImageEnhancerService(
            ReplicateClient replicateClient,
            EnhancementRequestRepository repository) {

        this.replicateClient = replicateClient;

        this.repository = repository;
    }


    public EnhancementResponse enhanceImage(
            MultipartFile image) {

        validateImage(image);


        long startTime =
                System.currentTimeMillis();


        EnhancementRequest request =
                new EnhancementRequest();


        request.setOriginalFileName(
                image.getOriginalFilename()
        );

        request.setContentType(
                image.getContentType()
        );

        request.setFileSize(
                image.getSize()
        );

        request.setStatus(
                "PROCESSING"
        );

        request.setCreatedAt(
                LocalDateTime.now()
        );


        EnhancementRequest savedRequest =
                repository.save(request);


        try {

            // Convert uploaded image
            // into a Base64 data URL

            String imageDataUrl =
                    convertToDataUrl(image);


            // Send image to Replicate

            String enhancedImageUrl =
                    replicateClient.enhanceImage(
                            imageDataUrl
                    );


            long processingTime =
                    System.currentTimeMillis()
                            - startTime;


            // Update database

            savedRequest.setStatus(
                    "COMPLETED"
            );

            savedRequest.setProcessingTimeMs(
                    processingTime
            );

            repository.save(savedRequest);


            // Return response to frontend

            return new EnhancementResponse(
                    savedRequest.getId(),
                    "COMPLETED",
                    enhancedImageUrl,
                    processingTime
            );


        } catch (AIServiceException exception) {

            savedRequest.setStatus(
                    "FAILED"
            );

            repository.save(savedRequest);


            throw exception;


        } catch (Exception exception) {

            savedRequest.setStatus(
                    "FAILED"
            );

            repository.save(savedRequest);


            throw new AIServiceException(
                    "Image enhancement failed: "
                            + exception.getMessage(),
                    exception
            );
        }
    }


    private void validateImage(
            MultipartFile image) {

        if (image == null ||
                image.isEmpty()) {

            throw new InvalidImageException(
                    "Image file is required"
            );
        }


        String contentType =
                image.getContentType();


        if (contentType == null ||
                !contentType.startsWith("image/")) {

            throw new InvalidImageException(
                    "Only image files are allowed"
            );
        }


        // Replicate recommends data URLs
        // for small files <= 256 KB.

        if (image.getSize() > 256 * 1024) {

            throw new InvalidImageException(
                    "Please upload an image smaller than 256 KB"
            );
        }
    }


    private String convertToDataUrl(
            MultipartFile image)
            throws IOException {

        byte[] imageBytes =
                image.getBytes();


        String base64 =
                Base64.getEncoder()
                        .encodeToString(imageBytes);


        return "data:"
                + image.getContentType()
                + ";base64,"
                + base64;
    }
}