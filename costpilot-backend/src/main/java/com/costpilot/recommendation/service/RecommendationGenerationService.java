package com.costpilot.recommendation.service;

import com.costpilot.aws.entity.AwsResource;
import com.costpilot.aws.entity.ResourceMetric;
import com.costpilot.aws.repository.AwsResourceRepository;
import com.costpilot.aws.repository.ResourceMetricRepository;
import com.costpilot.recommendation.entity.Recommendation;
import com.costpilot.recommendation.entity.RecommendationEvidence;
import com.costpilot.recommendation.repository.RecommendationEvidenceRepository;
import com.costpilot.recommendation.repository.RecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationGenerationService {

    private final AwsResourceRepository awsResourceRepository;
    private final ResourceMetricRepository resourceMetricRepository;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationEvidenceRepository recommendationEvidenceRepository;

    public RecommendationGenerationService(
            AwsResourceRepository awsResourceRepository,
            ResourceMetricRepository resourceMetricRepository,
            RecommendationRepository recommendationRepository,
            RecommendationEvidenceRepository recommendationEvidenceRepository
    ) {
        this.awsResourceRepository = awsResourceRepository;
        this.resourceMetricRepository = resourceMetricRepository;
        this.recommendationRepository = recommendationRepository;
        this.recommendationEvidenceRepository =
                recommendationEvidenceRepository;
    }

    @Transactional
    public int generateEc2Recommendations(UUID awsAccountId) {

        List<AwsResource> resources =
                awsResourceRepository.findByAwsAccount_Id(awsAccountId);

        int generatedCount = 0;

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(1);

        for (AwsResource resource : resources) {

            if (!"EC2".equalsIgnoreCase(resource.getServiceName())) {
                continue;
            }

            if (!"INSTANCE".equalsIgnoreCase(resource.getResourceType())) {
                continue;
            }

            if (!"running".equalsIgnoreCase(resource.getStatus())) {
                continue;
            }

            List<ResourceMetric> cpuMetrics =
                    resourceMetricRepository
                            .findByResource_IdAndMetricNameAndTimestampBetween(
                                    resource.getId(),
                                    "CPUUtilization",
                                    startTime,
                                    endTime
                            );

            if (cpuMetrics.isEmpty()) {
                continue;
            }

            BigDecimal averageCpu =
                    calculateAverageCpu(cpuMetrics);

            if (averageCpu.compareTo(
                    BigDecimal.valueOf(20)
            ) >= 0) {
                continue;
            }

            if (recommendationExists(resource.getId())) {
                continue;
            }

            Recommendation recommendation =
                    createRightsizingRecommendation(
                            resource,
                            averageCpu
                    );

            Recommendation savedRecommendation =
                    recommendationRepository.save(
                            recommendation
                    );

            saveCpuEvidence(
                    savedRecommendation,
                    averageCpu,
                    startTime,
                    endTime
            );

            generatedCount++;
        }

        return generatedCount;
    }

    private BigDecimal calculateAverageCpu(
            List<ResourceMetric> metrics
    ) {

        BigDecimal total = BigDecimal.ZERO;
        int count = 0;

        for (ResourceMetric metric : metrics) {

            if (metric.getValue() == null) {
                continue;
            }

            total = total.add(
                    BigDecimal.valueOf(
                            metric.getValue()
                    )
            );

            count++;
        }

        if (count == 0) {
            return BigDecimal.ZERO;
        }

        return total.divide(
                BigDecimal.valueOf(count),
                2,
                RoundingMode.HALF_UP
        );
    }

    private boolean recommendationExists(
            UUID resourceId
    ) {

        return recommendationRepository
                .findByResource_Id(resourceId)
                .stream()
                .anyMatch(recommendation ->
                        "RIGHTSIZE_EC2".equals(
                                recommendation.getRecommendationType()
                        )
                        &&
                        !"REJECTED".equalsIgnoreCase(
                                recommendation.getStatus()
                        )
                );
    }

    private Recommendation createRightsizingRecommendation(
            AwsResource resource,
            BigDecimal averageCpu
    ) {

        Recommendation recommendation =
                new Recommendation();

        recommendation.setAwsAccount(
                resource.getAwsAccount()
        );

        recommendation.setResource(
                resource
        );

        recommendation.setRecommendationType(
                "RIGHTSIZE_EC2"
        );

        recommendation.setTitle(
                "EC2 instance may be oversized"
        );

        recommendation.setDescription(
                "The EC2 instance has low average CPU utilization "
                        + "over the last hour and may be a candidate "
                        + "for rightsizing."
        );

        recommendation.setCurrentConfiguration(
                "{\"instanceType\":\""
                        + escapeJson(
                                resource.getInstanceType()
                        )
                        + "\"}"
        );

        recommendation.setRecommendedConfiguration(
                "{\"action\":\"review_downsizing\"}"
        );

        recommendation.setCurrentMonthlyCost(
                BigDecimal.ZERO
        );

        recommendation.setEstimatedMonthlyCost(
                BigDecimal.ZERO
        );

        recommendation.setEstimatedMonthlySavings(
                BigDecimal.ZERO
        );

        recommendation.setEstimatedAnnualSavings(
                BigDecimal.ZERO
        );

        recommendation.setConfidenceScore(
                calculateConfidence(averageCpu)
        );

        recommendation.setRiskLevel(
                "LOW"
        );

        recommendation.setEnvironment(
                resource.getEnvironment()
        );

        recommendation.setProductionDependency(
                false
        );

        recommendation.setTerraformManaged(
                resource.getTerraformManaged()
        );

        recommendation.setReasoning(
                "Average CPU utilization over the last hour is "
                        + averageCpu
                        + "%. The configured rule considers "
                        + "running EC2 instances below 20% average CPU "
                        + "as candidates for rightsizing review."
        );

        recommendation.setStatus(
                "PENDING"
        );

        recommendation.setGeneratedBy(
                "RULE_ENGINE"
        );

        LocalDateTime now =
                LocalDateTime.now();

        recommendation.setCreatedAt(now);
        recommendation.setUpdatedAt(now);

        return recommendation;
    }

    private void saveCpuEvidence(
            Recommendation recommendation,
            BigDecimal averageCpu,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        RecommendationEvidence evidence =
                new RecommendationEvidence();

        evidence.setRecommendation(
                recommendation
        );

        evidence.setEvidenceType(
                "CPU_UTILIZATION"
        );

        evidence.setMetricName(
                "CPUUtilization"
        );

        evidence.setMetricValue(
                averageCpu
        );

        evidence.setMetricUnit(
                "Percent"
        );

        evidence.setObservationStart(
                startTime
        );

        evidence.setObservationEnd(
                endTime
        );

        evidence.setDescription(
                "Average CPU utilization was "
                        + averageCpu
                        + "% over the last hour."
        );

        evidence.setMetadata(
                "{\"threshold\":20,"
                        + "\"statistic\":\"Average\","
                        + "\"period\":\"1 hour\"}"
        );

        evidence.setCreatedAt(
                LocalDateTime.now()
        );

        recommendationEvidenceRepository.save(
                evidence
        );
    }

    private BigDecimal calculateConfidence(
            BigDecimal averageCpu
    ) {

        if (averageCpu.compareTo(
                BigDecimal.valueOf(10)
        ) < 0) {

            return BigDecimal.valueOf(0.90);
        }

        if (averageCpu.compareTo(
                BigDecimal.valueOf(15)
        ) < 0) {

            return BigDecimal.valueOf(0.80);
        }

        return BigDecimal.valueOf(0.70);
    }

    private String escapeJson(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}