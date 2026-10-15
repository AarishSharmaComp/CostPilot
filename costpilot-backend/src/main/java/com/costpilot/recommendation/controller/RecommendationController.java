package com.costpilot.recommendation.controller;

import com.costpilot.recommendation.dto.RecommendationResponse;
import com.costpilot.recommendation.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(
            RecommendationService recommendationService
    ) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<RecommendationResponse> getAllRecommendations() {
        return recommendationService.getAllRecommendations();
    }

    @GetMapping("/account/{accountId}")
    public List<RecommendationResponse> getRecommendationsByAccount(
            @PathVariable UUID accountId
    ) {
        return recommendationService
                .getRecommendationsByAccount(accountId);
    }

    @GetMapping("/resource/{resourceId}")
    public List<RecommendationResponse> getRecommendationsByResource(
            @PathVariable UUID resourceId
    ) {
        return recommendationService
                .getRecommendationsByResource(resourceId);
    }

    @GetMapping("/status/{status}")
    public List<RecommendationResponse> getRecommendationsByStatus(
            @PathVariable String status
    ) {
        return recommendationService
                .getRecommendationsByStatus(status);
    }
}