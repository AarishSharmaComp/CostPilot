package com.costpilot.aws.controller;

import com.costpilot.aws.dto.ResourceMetricResponse;
import com.costpilot.aws.service.ResourceMetricQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/aws/resources")
public class ResourceMetricController {

    private final ResourceMetricQueryService resourceMetricQueryService;

    public ResourceMetricController(
            ResourceMetricQueryService resourceMetricQueryService
    ) {
        this.resourceMetricQueryService = resourceMetricQueryService;
    }

    @GetMapping("/{resourceId}/metrics")
    public List<ResourceMetricResponse> getMetrics(
            @PathVariable UUID resourceId
    ) {
        return resourceMetricQueryService.getMetrics(resourceId);
    }

    @GetMapping("/{resourceId}/metrics/{metricName}")
    public List<ResourceMetricResponse> getMetricsByName(
            @PathVariable UUID resourceId,
            @PathVariable String metricName
    ) {
        return resourceMetricQueryService.getMetricsByName(
                resourceId,
                metricName
        );
    }
}