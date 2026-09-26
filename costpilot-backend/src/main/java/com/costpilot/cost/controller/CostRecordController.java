package com.costpilot.cost.controller;

import com.costpilot.cost.service.CostRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/aws/costs")
public class CostRecordController {

    private final CostRecordService costRecordService;

    public CostRecordController(CostRecordService costRecordService) {
        this.costRecordService = costRecordService;
    }

    @PostMapping("/sync/{accountId}")
    public String syncCosts(@PathVariable UUID accountId) {

        int savedCount = costRecordService.syncCosts(accountId);

        return "Synchronized " + savedCount + " cost records";
    }
}