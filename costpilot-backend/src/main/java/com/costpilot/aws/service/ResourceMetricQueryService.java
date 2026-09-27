package com.costpilot.aws.service;

import com.costpilot.aws.dto.ResourceMetricResponse;
import com.costpilot.aws.entity.ResourceMetric;
import com.costpilot.aws.repository.ResourceMetricRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ResourceMetricQueryService {

    private final ResourceMetricRepository resourceMetricRepository;

    public ResourceMetricQueryService(
            ResourceMetricRepository resourceMetricRepository
    ) {
        this.resourceMetricRepository = resourceMetricRepository;
    }

    public List<ResourceMetricResponse> getMetrics(UUID resourceId) {

        return resourceMetricRepository
                .findByResource_Id(resourceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ResourceMetricResponse> getMetricsByName(
            UUID resourceId,
            String metricName
    ) {

        return resourceMetricRepository
                .findByResource_IdAndMetricName(
                        resourceId,
                        metricName
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ResourceMetricResponse toResponse(
            ResourceMetric metric
    ) {

        return new ResourceMetricResponse(
                metric.getId(),
                metric.getResource().getId(),
                metric.getMetricName(),
                metric.getMetricNamespace(),
                metric.getTimestamp(),
                metric.getValue(),
                metric.getUnit(),
                metric.getStatistic(),
                metric.getCreatedAt()
        );
    }
}