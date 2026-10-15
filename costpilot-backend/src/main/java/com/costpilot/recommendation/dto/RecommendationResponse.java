package com.costpilot.recommendation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class RecommendationResponse {

    private UUID id;
    private UUID awsAccountId;
    private UUID resourceId;

    private String recommendationType;
    private String title;
    private String description;

    private String currentConfiguration;
    private String recommendedConfiguration;

    private BigDecimal currentMonthlyCost;
    private BigDecimal estimatedMonthlyCost;
    private BigDecimal estimatedMonthlySavings;
    private BigDecimal estimatedAnnualSavings;

    private BigDecimal confidenceScore;
    private String riskLevel;
    private String environment;

    private Boolean productionDependency;
    private Boolean terraformManaged;

    private String reasoning;
    private String status;
    private String generatedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RecommendationResponse() {
    }

    public RecommendationResponse(
            UUID id,
            UUID awsAccountId,
            UUID resourceId,
            String recommendationType,
            String title,
            String description,
            String currentConfiguration,
            String recommendedConfiguration,
            BigDecimal currentMonthlyCost,
            BigDecimal estimatedMonthlyCost,
            BigDecimal estimatedMonthlySavings,
            BigDecimal estimatedAnnualSavings,
            BigDecimal confidenceScore,
            String riskLevel,
            String environment,
            Boolean productionDependency,
            Boolean terraformManaged,
            String reasoning,
            String status,
            String generatedBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.awsAccountId = awsAccountId;
        this.resourceId = resourceId;
        this.recommendationType = recommendationType;
        this.title = title;
        this.description = description;
        this.currentConfiguration = currentConfiguration;
        this.recommendedConfiguration = recommendedConfiguration;
        this.currentMonthlyCost = currentMonthlyCost;
        this.estimatedMonthlyCost = estimatedMonthlyCost;
        this.estimatedMonthlySavings = estimatedMonthlySavings;
        this.estimatedAnnualSavings = estimatedAnnualSavings;
        this.confidenceScore = confidenceScore;
        this.riskLevel = riskLevel;
        this.environment = environment;
        this.productionDependency = productionDependency;
        this.terraformManaged = terraformManaged;
        this.reasoning = reasoning;
        this.status = status;
        this.generatedBy = generatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAwsAccountId() {
        return awsAccountId;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrentConfiguration() {
        return currentConfiguration;
    }

    public String getRecommendedConfiguration() {
        return recommendedConfiguration;
    }

    public BigDecimal getCurrentMonthlyCost() {
        return currentMonthlyCost;
    }

    public BigDecimal getEstimatedMonthlyCost() {
        return estimatedMonthlyCost;
    }

    public BigDecimal getEstimatedMonthlySavings() {
        return estimatedMonthlySavings;
    }

    public BigDecimal getEstimatedAnnualSavings() {
        return estimatedAnnualSavings;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getEnvironment() {
        return environment;
    }

    public Boolean getProductionDependency() {
        return productionDependency;
    }

    public Boolean getTerraformManaged() {
        return terraformManaged;
    }

    public String getReasoning() {
        return reasoning;
    }

    public String getStatus() {
        return status;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}