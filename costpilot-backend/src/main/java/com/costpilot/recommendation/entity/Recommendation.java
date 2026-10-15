package com.costpilot.recommendation.entity;

import com.costpilot.aws.entity.AwsAccount;
import com.costpilot.aws.entity.AwsResource;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aws_account_id", nullable = false)
    private AwsAccount awsAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private AwsResource resource;

    @Column(name = "recommendation_type", nullable = false)
    private String recommendationType;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(
            name = "current_configuration",
            columnDefinition = "jsonb"
    )
    private String currentConfiguration;

    @Column(
            name = "recommended_configuration",
            columnDefinition = "jsonb"
    )
    private String recommendedConfiguration;

    @Column(name = "current_monthly_cost")
    private BigDecimal currentMonthlyCost;

    @Column(name = "estimated_monthly_cost")
    private BigDecimal estimatedMonthlyCost;

    @Column(name = "estimated_monthly_savings")
    private BigDecimal estimatedMonthlySavings;

    @Column(name = "estimated_annual_savings")
    private BigDecimal estimatedAnnualSavings;

    @Column(name = "confidence_score")
    private BigDecimal confidenceScore;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "environment")
    private String environment;

    @Column(name = "production_dependency")
    private Boolean productionDependency;

    @Column(name = "terraform_managed")
    private Boolean terraformManaged;

    @Column(columnDefinition = "text")
    private String reasoning;

    @Column(nullable = false)
    private String status;

    @Column(name = "generated_by")
    private String generatedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Recommendation() {
    }

    public UUID getId() {
        return id;
    }

    public AwsAccount getAwsAccount() {
        return awsAccount;
    }

    public void setAwsAccount(AwsAccount awsAccount) {
        this.awsAccount = awsAccount;
    }

    public AwsResource getResource() {
        return resource;
    }

    public void setResource(AwsResource resource) {
        this.resource = resource;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrentConfiguration() {
        return currentConfiguration;
    }

    public void setCurrentConfiguration(String currentConfiguration) {
        this.currentConfiguration = currentConfiguration;
    }

    public String getRecommendedConfiguration() {
        return recommendedConfiguration;
    }

    public void setRecommendedConfiguration(String recommendedConfiguration) {
        this.recommendedConfiguration = recommendedConfiguration;
    }

    public BigDecimal getCurrentMonthlyCost() {
        return currentMonthlyCost;
    }

    public void setCurrentMonthlyCost(BigDecimal currentMonthlyCost) {
        this.currentMonthlyCost = currentMonthlyCost;
    }

    public BigDecimal getEstimatedMonthlyCost() {
        return estimatedMonthlyCost;
    }

    public void setEstimatedMonthlyCost(BigDecimal estimatedMonthlyCost) {
        this.estimatedMonthlyCost = estimatedMonthlyCost;
    }

    public BigDecimal getEstimatedMonthlySavings() {
        return estimatedMonthlySavings;
    }

    public void setEstimatedMonthlySavings(BigDecimal estimatedMonthlySavings) {
        this.estimatedMonthlySavings = estimatedMonthlySavings;
    }

    public BigDecimal getEstimatedAnnualSavings() {
        return estimatedAnnualSavings;
    }

    public void setEstimatedAnnualSavings(BigDecimal estimatedAnnualSavings) {
        this.estimatedAnnualSavings = estimatedAnnualSavings;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Boolean getProductionDependency() {
        return productionDependency;
    }

    public void setProductionDependency(Boolean productionDependency) {
        this.productionDependency = productionDependency;
    }

    public Boolean getTerraformManaged() {
        return terraformManaged;
    }

    public void setTerraformManaged(Boolean terraformManaged) {
        this.terraformManaged = terraformManaged;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}