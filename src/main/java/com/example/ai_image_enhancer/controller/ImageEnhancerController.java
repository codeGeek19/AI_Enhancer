package com.example.ai_image_enhancer.controller;

import com.example.ai_image_enhancer.dto.EnhancementResponse;
import com.example.ai_image_enhancer.service.ImageEnhancerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageEnhancerController {

    private final ImageEnhancerService imageEnhancerService;

    public ImageEnhancerController(
            ImageEnhancerService imageEnhancerService) {

        this.imageEnhancerService =
                imageEnhancerService;
    }

    @PostMapping("/enhance")
    public ResponseEntity<EnhancementResponse> enhanceImage(
            @RequestParam("image")
            MultipartFile image) {

        EnhancementResponse response =
                imageEnhancerService.enhanceImage(image);

        return ResponseEntity.ok(response);
    }
}