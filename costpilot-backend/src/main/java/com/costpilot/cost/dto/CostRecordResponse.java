package com.costpilot.cost.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CostRecordResponse {

    private Long id;
    private LocalDate billingDate;
    private String serviceName;
    private String region;
    private String usageType;
    private BigDecimal usageQuantity;
    private String usageUnit;
    private BigDecimal unblendedCost;
    private BigDecimal blendedCost;
    private String currency;

    public CostRecordResponse() {
    }

    public CostRecordResponse(
            Long id,
            LocalDate billingDate,
            String serviceName,
            String region,
            String usageType,
            BigDecimal usageQuantity,
            String usageUnit,
            BigDecimal unblendedCost,
            BigDecimal blendedCost,
            String currency
    ) {
        this.id = id;
        this.billingDate = billingDate;
        this.serviceName = serviceName;
        this.region = region;
        this.usageType = usageType;
        this.usageQuantity = usageQuantity;
        this.usageUnit = usageUnit;
        this.unblendedCost = unblendedCost;
        this.blendedCost = blendedCost;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getBillingDate() {
        return billingDate;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getRegion() {
        return region;
    }

    public String getUsageType() {
        return usageType;
    }

    public BigDecimal getUsageQuantity() {
        return usageQuantity;
    }

    public String getUsageUnit() {
        return usageUnit;
    }

    public BigDecimal getUnblendedCost() {
        return unblendedCost;
    }

    public BigDecimal getBlendedCost() {
        return blendedCost;
    }

    public String getCurrency() {
        return currency;
    }
}