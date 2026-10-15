package com.costpilot.recommendation.repository;

import com.costpilot.recommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecommendationRepository
        extends JpaRepository<Recommendation, UUID> {

    List<Recommendation> findByAwsAccount_Id(UUID awsAccountId);

    List<Recommendation> findByResource_Id(UUID resourceId);

    List<Recommendation> findByStatus(String status);

    List<Recommendation> findByAwsAccount_IdAndStatus(
            UUID awsAccountId,
            String status
    );
}