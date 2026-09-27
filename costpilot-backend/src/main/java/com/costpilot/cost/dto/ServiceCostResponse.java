package com.costpilot.cost.dto;

import java.math.BigDecimal;

public class ServiceCostResponse {

    private String serviceName;
    private BigDecimal totalCost;
    private String currency;

    public ServiceCostResponse() {
    }

    public ServiceCostResponse(
            String serviceName,
            BigDecimal totalCost,
            String currency
    ) {
        this.serviceName = serviceName;
        this.totalCost = totalCost;
        this.currency = currency;
    }

    public String getServiceName() {
        return serviceName;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public String getCurrency() {
        return currency;
    }
}