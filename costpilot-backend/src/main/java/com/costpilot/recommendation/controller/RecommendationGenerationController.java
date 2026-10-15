package com.costpilot.recommendation.controller;

import com.costpilot.recommendation.service.RecommendationGenerationService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationGenerationController {

    private final RecommendationGenerationService recommendationGenerationService;

    public RecommendationGenerationController(
            RecommendationGenerationService recommendationGenerationService
    ) {
        this.recommendationGenerationService =
                recommendationGenerationService;
    }

    @PostMapping("/generate/{accountId}")
    public String generateRecommendations(
            @PathVariable UUID accountId
    ) {

        int generatedCount =
                recommendationGenerationService
                        .generateEc2Recommendations(accountId);

        return "Generated " + generatedCount + " recommendations";
    }
}