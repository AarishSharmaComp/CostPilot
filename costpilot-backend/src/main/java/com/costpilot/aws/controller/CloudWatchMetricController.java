package com.costpilot.aws.controller;

import com.costpilot.aws.service.CloudWatchMetricService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/aws/resources")
public class CloudWatchMetricController {

    private final CloudWatchMetricService cloudWatchMetricService;

    public CloudWatchMetricController(
            CloudWatchMetricService cloudWatchMetricService
    ) {
        this.cloudWatchMetricService = cloudWatchMetricService;
    }

    @PostMapping("/{resourceId}/metrics/cpu/sync")
    public String syncCpuMetrics(
            @PathVariable UUID resourceId
    ) {

        int savedCount =
                cloudWatchMetricService.syncCpuMetrics(resourceId);

        return "Synchronized " + savedCount + " CPU metric records";
    }
}