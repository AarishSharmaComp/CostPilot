package com.costpilot.aws.controller;

import com.costpilot.aws.service.CostExplorerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/aws/costs")
public class CostExplorerController {

    private final CostExplorerService costExplorerService;

    public CostExplorerController(CostExplorerService costExplorerService) {
        this.costExplorerService = costExplorerService;
    }

    @GetMapping("/recent")
    public List<String> getRecentCosts() {
        return costExplorerService.getRecentCosts();
    }
}