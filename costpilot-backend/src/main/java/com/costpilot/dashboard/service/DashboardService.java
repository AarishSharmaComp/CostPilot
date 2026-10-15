package com.costpilot.dashboard.service;

import com.costpilot.cost.dto.CostSummaryResponse;
import com.costpilot.cost.dto.CostTrendResponse;
import com.costpilot.cost.dto.ServiceCostResponse;
import com.costpilot.cost.service.CostQueryService;
import com.costpilot.aws.repository.AwsResourceRepository;
import com.costpilot.dashboard.dto.DashboardResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    private final CostQueryService costQueryService;
    private final AwsResourceRepository awsResourceRepository;

    public DashboardService(
            CostQueryService costQueryService,
            AwsResourceRepository awsResourceRepository
    ) {
        this.costQueryService = costQueryService;
        this.awsResourceRepository = awsResourceRepository;
    }

    public DashboardResponse getDashboard() {

        CostSummaryResponse summary =
                costQueryService.getCostSummary();

        BigDecimal monthlyUsage =
                costQueryService.getMonthlyGrossUsage();

        List<CostTrendResponse> costTrend =
                costQueryService.getCostTrend();

        List<ServiceCostResponse> serviceBreakdown =
                costQueryService.getServiceCostBreakdown();

        long resourceCount =
                awsResourceRepository.count();

        BigDecimal potentialSavings =
                BigDecimal.ZERO;

        BigDecimal realizedSavings =
                BigDecimal.ZERO;

        return new DashboardResponse(
                monthlyUsage,
                summary.getCurrency(),
                resourceCount,
                potentialSavings,
                realizedSavings,
                costTrend,
                serviceBreakdown
        );
    }
}