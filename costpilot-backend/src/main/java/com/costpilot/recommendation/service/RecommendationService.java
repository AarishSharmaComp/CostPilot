package com.costpilot.recommendation.service;

import com.costpilot.recommendation.dto.RecommendationResponse;
import com.costpilot.recommendation.entity.Recommendation;
import com.costpilot.recommendation.repository.RecommendationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public RecommendationService(
            RecommendationRepository recommendationRepository
    ) {
        this.recommendationRepository = recommendationRepository;
    }

    public List<RecommendationResponse> getAllRecommendations() {

        return recommendationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RecommendationResponse> getRecommendationsByAccount(
            UUID accountId
    ) {

        return recommendationRepository
                .findByAwsAccount_Id(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RecommendationResponse> getRecommendationsByResource(
            UUID resourceId
    ) {

        return recommendationRepository
                .findByResource_Id(resourceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RecommendationResponse> getRecommendationsByStatus(
            String status
    ) {

        return recommendationRepository
                .findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RecommendationResponse toResponse(
            Recommendation recommendation
    ) {

        UUID accountId = recommendation.getAwsAccount() != null
                ? recommendation.getAwsAccount().getId()
                : null;

        UUID resourceId = recommendation.getResource() != null
                ? recommendation.getResource().getId()
                : null;

        return new RecommendationResponse(
                recommendation.getId(),
                accountId,
                resourceId,
                recommendation.getRecommendationType(),
                recommendation.getTitle(),
                recommendation.getDescription(),
                recommendation.getCurrentConfiguration(),
                recommendation.getRecommendedConfiguration(),
                recommendation.getCurrentMonthlyCost(),
                recommendation.getEstimatedMonthlyCost(),
                recommendation.getEstimatedMonthlySavings(),
                recommendation.getEstimatedAnnualSavings(),
                recommendation.getConfidenceScore(),
                recommendation.getRiskLevel(),
                recommendation.getEnvironment(),
                recommendation.getProductionDependency(),
                recommendation.getTerraformManaged(),
                recommendation.getReasoning(),
                recommendation.getStatus(),
                recommendation.getGeneratedBy(),
                recommendation.getCreatedAt(),
                recommendation.getUpdatedAt()
        );
    }
}