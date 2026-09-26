package com.costpilot.cost.entity;

import com.costpilot.aws.entity.AwsAccount;
import com.costpilot.aws.entity.AwsResource;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cost_records")
public class CostRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aws_account_id", nullable = false)
    private AwsAccount awsAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private AwsResource resource;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "region")
    private String region;

    @Column(name = "usage_type")
    private String usageType;

    @Column(name = "billing_date", nullable = false)
    private LocalDate billingDate;

    @Column(name = "usage_quantity")
    private BigDecimal usageQuantity;

    @Column(name = "usage_unit")
    private String usageUnit;

    @Column(name = "unblended_cost")
    private BigDecimal unblendedCost;

    @Column(name = "blended_cost")
    private BigDecimal blendedCost;

    @Column(name = "currency")
    private String currency;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public CostRecord() {
    }

    public Long getId() {
        return id;
    }

    public AwsAccount getAwsAccount() {
        return awsAccount;
    }

    public void setAwsAccount(AwsAccount awsAccount) {
        this.awsAccount = awsAccount;
    }

    public AwsResource getResource() {
        return resource;
    }

    public void setResource(AwsResource resource) {
        this.resource = resource;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public LocalDate getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(LocalDate billingDate) {
        this.billingDate = billingDate;
    }

    public BigDecimal getUsageQuantity() {
        return usageQuantity;
    }

    public void setUsageQuantity(BigDecimal usageQuantity) {
        this.usageQuantity = usageQuantity;
    }

    public String getUsageUnit() {
        return usageUnit;
    }

    public void setUsageUnit(String usageUnit) {
        this.usageUnit = usageUnit;
    }

    public BigDecimal getUnblendedCost() {
        return unblendedCost;
    }

    public void setUnblendedCost(BigDecimal unblendedCost) {
        this.unblendedCost = unblendedCost;
    }

    public BigDecimal getBlendedCost() {
        return blendedCost;
    }

    public void setBlendedCost(BigDecimal blendedCost) {
        this.blendedCost = blendedCost;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}