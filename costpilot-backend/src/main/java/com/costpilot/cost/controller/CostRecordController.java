package com.costpilot.cost.controller;
import com.costpilot.cost.dto.ServiceCostResponse;
import com.costpilot.cost.dto.CostRecordResponse;
import com.costpilot.cost.dto.CostSummaryResponse;
import com.costpilot.cost.service.CostQueryService;
import com.costpilot.cost.service.CostRecordService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/aws/costs")
public class CostRecordController {

    private final CostRecordService costRecordService;
    private final CostQueryService costQueryService;

    public CostRecordController(
            CostRecordService costRecordService,
            CostQueryService costQueryService
    ) {
        this.costRecordService = costRecordService;
        this.costQueryService = costQueryService;
    }

    @PostMapping("/sync/{accountId}")
    public String syncCosts(@PathVariable UUID accountId) {

        int savedCount = costRecordService.syncCosts(accountId);

        return "Synchronized " + savedCount + " cost records";
    }

    @GetMapping
    public List<CostRecordResponse> getAllCosts() {
        return costQueryService.getAllCosts();
    }

    @GetMapping("/account/{accountId}")
    public List<CostRecordResponse> getCostsByAccount(
            @PathVariable UUID accountId
    ) {
        return costQueryService.getCostsByAccount(accountId);
    }
    @GetMapping("/summary")
    public CostSummaryResponse getCostSummary() {
        return costQueryService.getCostSummary();
    }
    @GetMapping("/service-breakdown")
    public List<ServiceCostResponse> getServiceCostBreakdown() {
        return costQueryService.getServiceCostBreakdown();
    }
}