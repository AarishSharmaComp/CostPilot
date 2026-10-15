package com.costpilot.cost.dto;

import java.math.BigDecimal;

public class CostSummaryResponse {

    private BigDecimal grossUsage;
    private BigDecimal credits;
    private BigDecimal tax;
    private BigDecimal netCost;
    private String currency;
    private long recordCount;

    public CostSummaryResponse() {
    }

    public CostSummaryResponse(
            BigDecimal grossUsage,
            BigDecimal credits,
            BigDecimal tax,
            BigDecimal netCost,
            String currency,
            long recordCount
    ) {
        this.grossUsage = grossUsage;
        this.credits = credits;
        this.tax = tax;
        this.netCost = netCost;
        this.currency = currency;
        this.recordCount = recordCount;
    }

    public BigDecimal getGrossUsage() {
        return grossUsage;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public BigDecimal getNetCost() {
        return netCost;
    }

    public String getCurrency() {
        return currency;
    }

    public long getRecordCount() {
        return recordCount;
    }
}