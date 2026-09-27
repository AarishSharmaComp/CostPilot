package com.costpilot.aws.repository;

import com.costpilot.aws.entity.ResourceMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ResourceMetricRepository extends JpaRepository<ResourceMetric, Long> {

    List<ResourceMetric> findByResource_Id(UUID resourceId);

    List<ResourceMetric> findByResource_IdAndMetricName(
            UUID resourceId,
            String metricName
    );

    List<ResourceMetric> findByResource_IdAndMetricNameAndTimestampBetween(
            UUID resourceId,
            String metricName,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}