package com.costpilot.recommendation.repository;

import com.costpilot.recommendation.entity.RecommendationEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecommendationEvidenceRepository
        extends JpaRepository<RecommendationEvidence, UUID> {

    List<RecommendationEvidence> findByRecommendation_Id(
            UUID recommendationId
    );
}