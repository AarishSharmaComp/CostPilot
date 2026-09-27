package com.costpilot.aws.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ResourceMetricResponse {

    private Long id;
    private UUID resourceId;
    private String metricName;
    private String metricNamespace;
    private LocalDateTime timestamp;
    private Double value;
    private String unit;
    private String statistic;
    private LocalDateTime createdAt;

    public ResourceMetricResponse() {
    }

    public ResourceMetricResponse(
            Long id,
            UUID resourceId,
            String metricName,
            String metricNamespace,
            LocalDateTime timestamp,
            Double value,
            String unit,
            String statistic,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.resourceId = resourceId;
        this.metricName = metricName;
        this.metricNamespace = metricNamespace;
        this.timestamp = timestamp;
        this.value = value;
        this.unit = unit;
        this.statistic = statistic;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getMetricNamespace() {
        return metricNamespace;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public String getStatistic() {
        return statistic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}