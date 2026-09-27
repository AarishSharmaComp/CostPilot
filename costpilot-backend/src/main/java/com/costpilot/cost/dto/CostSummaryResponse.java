package com.costpilot.cost.dto;

import java.math.BigDecimal;

public class CostSummaryResponse {

    private BigDecimal totalCost;
    private String currency;
    private long recordCount;

    public CostSummaryResponse() {
    }

    public CostSummaryResponse(
            BigDecimal totalCost,
            String currency,
            long recordCount
    ) {
        this.totalCost = totalCost;
        this.currency = currency;
        this.recordCount = recordCount;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public String getCurrency() {
        return currency;
    }

    public long getRecordCount() {
        return recordCount;
    }
}