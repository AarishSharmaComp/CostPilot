package com.costpilot.recommendation.controller;

import com.costpilot.recommendation.entity.RecommendationEvidence;
import com.costpilot.recommendation.repository.RecommendationEvidenceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationEvidenceController {

    private final RecommendationEvidenceRepository
            recommendationEvidenceRepository;

    public RecommendationEvidenceController(
            RecommendationEvidenceRepository recommendationEvidenceRepository
    ) {
        this.recommendationEvidenceRepository =
                recommendationEvidenceRepository;
    }

    @GetMapping("/{recommendationId}/evidence")
    public List<RecommendationEvidence> getEvidence(
            @PathVariable UUID recommendationId
    ) {

        return recommendationEvidenceRepository
                .findByRecommendation_Id(recommendationId);
    }
}