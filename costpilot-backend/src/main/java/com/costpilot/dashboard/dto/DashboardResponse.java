package com.costpilot.dashboard.dto;

import com.costpilot.cost.dto.CostTrendResponse;
import com.costpilot.cost.dto.ServiceCostResponse;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    private BigDecimal monthlyUsage;
    private String currency;
    private long resourceCount;
    private BigDecimal potentialSavings;
    private BigDecimal realizedSavings;
    private List<CostTrendResponse> costTrend;
    private List<ServiceCostResponse> serviceBreakdown;

    public DashboardResponse() {
    }

    public DashboardResponse(
            BigDecimal monthlyUsage,
            String currency,
            long resourceCount,
            BigDecimal potentialSavings,
            BigDecimal realizedSavings,
            List<CostTrendResponse> costTrend,
            List<ServiceCostResponse> serviceBreakdown
    ) {
        this.monthlyUsage = monthlyUsage;
        this.currency = currency;
        this.resourceCount = resourceCount;
        this.potentialSavings = potentialSavings;
        this.realizedSavings = realizedSavings;
        this.costTrend = costTrend;
        this.serviceBreakdown = serviceBreakdown;
    }

    public BigDecimal getMonthlyUsage() {
        return monthlyUsage;
    }

    public String getCurrency() {
        return currency;
    }

    public long getResourceCount() {
        return resourceCount;
    }

    public BigDecimal getPotentialSavings() {
        return potentialSavings;
    }

    public BigDecimal getRealizedSavings() {
        return realizedSavings;
    }

    public List<CostTrendResponse> getCostTrend() {
        return costTrend;
    }

    public List<ServiceCostResponse> getServiceBreakdown() {
        return serviceBreakdown;
    }
}