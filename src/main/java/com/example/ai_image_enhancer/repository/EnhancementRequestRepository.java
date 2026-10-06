package com.example.ai_image_enhancer.repository;

import com.example.ai_image_enhancer.entity.EnhancementRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnhancementRequestRepository
        extends JpaRepository<EnhancementRequest, Long> {
}
