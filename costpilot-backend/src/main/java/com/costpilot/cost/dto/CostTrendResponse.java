package com.costpilot.cost.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CostTrendResponse {

    private LocalDate date;
    private BigDecimal totalCost;
    private String currency;

    public CostTrendResponse() {
    }

    public CostTrendResponse(
            LocalDate date,
            BigDecimal totalCost,
            String currency
    ) {
        this.date = date;
        this.totalCost = totalCost;
        this.currency = currency;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public String getCurrency() {
        return currency;
    }
}