package com.costpilot.aws.controller;

import com.costpilot.aws.service.AwsRegionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aws/regions")
public class AwsRegionController {

    private final AwsRegionService awsRegionService;

    public AwsRegionController(AwsRegionService awsRegionService) {
        this.awsRegionService = awsRegionService;
    }

    @PostMapping("/sync")
    public String syncRegions() {

        int synchronizedCount =
                awsRegionService.syncRegions();

        return "Synchronized " + synchronizedCount + " AWS regions";
    }
}