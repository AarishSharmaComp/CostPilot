package com.costpilot.aws.controller;

import com.costpilot.aws.service.AwsResourceSyncService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/aws/accounts")
public class AwsResourceSyncController {

    private final AwsResourceSyncService awsResourceSyncService;

    public AwsResourceSyncController(
            AwsResourceSyncService awsResourceSyncService
    ) {
        this.awsResourceSyncService = awsResourceSyncService;
    }

    @PostMapping("/{accountId}/sync")
    public String syncAwsResources(
            @PathVariable UUID accountId
    ) {
        int synchronizedCount =
                awsResourceSyncService.syncEc2Resources(accountId);

        return "Synchronized " + synchronizedCount + " AWS resources";
    }
}