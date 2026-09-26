package com.costpilot.aws.controller;

import com.costpilot.aws.dto.AwsResourceResponse;
import com.costpilot.aws.service.AwsResourceQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/aws/resources")
public class AwsResourceQueryController {

    private final AwsResourceQueryService awsResourceQueryService;

    public AwsResourceQueryController(
            AwsResourceQueryService awsResourceQueryService
    ) {
        this.awsResourceQueryService = awsResourceQueryService;
    }

    @GetMapping
    public List<AwsResourceResponse> getAllResources() {
        return awsResourceQueryService.getAllResources();
    }

    @GetMapping("/account/{accountId}")
    public List<AwsResourceResponse> getResourcesByAccount(
            @PathVariable UUID accountId
    ) {
        return awsResourceQueryService.getResourcesByAccount(accountId);
    }
}