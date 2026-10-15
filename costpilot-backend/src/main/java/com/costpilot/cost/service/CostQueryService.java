package com.costpilot.cost.service;

import com.costpilot.cost.dto.CostTrendResponse;
import com.costpilot.cost.dto.CostRecordResponse;
import com.costpilot.cost.dto.CostSummaryResponse;
import com.costpilot.cost.dto.ServiceCostResponse;
import com.costpilot.cost.entity.CostRecord;
import com.costpilot.cost.repository.CostRecordRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CostQueryService {

    private final CostRecordRepository costRecordRepository;

    public CostQueryService(
            CostRecordRepository costRecordRepository
    ) {
        this.costRecordRepository = costRecordRepository;
    }

    public List<CostRecordResponse> getAllCosts() {
        return costRecordRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CostRecordResponse> getCostsByAccount(
            UUID accountId
    ) {
        return costRecordRepository
                .findByAwsAccount_Id(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CostSummaryResponse getCostSummary() {

        List<CostRecord> records =
                costRecordRepository.findAll();

        BigDecimal grossUsage = BigDecimal.ZERO;
        BigDecimal credits = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal otherAdjustments = BigDecimal.ZERO;

        for (CostRecord record : records) {

            BigDecimal cost = record.getUnblendedCost();

            if (cost == null) {
                continue;
            }

            String recordType = record.getRecordType();

            if (recordType == null) {
                recordType = "";
            }

            recordType = recordType.trim();

            if (recordType.equalsIgnoreCase("Usage")) {

                // Count positive usage charges as gross usage.
                if (cost.signum() > 0) {
                    grossUsage = grossUsage.add(cost);
                } else if (cost.signum() < 0) {
                    otherAdjustments =
                            otherAdjustments.add(cost);
                }

            } else if (recordType.equalsIgnoreCase("Credit")) {

                // Store credits as a positive display amount.
                credits = credits.add(cost.abs());

            } else if (recordType.equalsIgnoreCase("Tax")) {

                tax = tax.add(cost);

            } else {

                // Preserve other record types in the net calculation.
                otherAdjustments =
                        otherAdjustments.add(cost);
            }
        }

        /*
         * Net cost includes usage, credits, taxes and
         * other adjustments.
         */
        BigDecimal netCost = grossUsage
                .subtract(credits)
                .add(tax)
                .add(otherAdjustments);

        String currency = records.stream()
                .map(CostRecord::getCurrency)
                .filter(value ->
                        value != null && !value.isBlank())
                .findFirst()
                .orElse("USD");

        return new CostSummaryResponse(
                grossUsage,
                credits,
                tax,
                netCost,
                currency,
                records.size()
        );
    }
        public BigDecimal getMonthlyGrossUsage() {

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        List<CostRecord> records =
                costRecordRepository.findAll();

        BigDecimal monthlyGrossUsage = BigDecimal.ZERO;

        for (CostRecord record : records) {

                if (record.getBillingDate() == null ||
                        record.getUnblendedCost() == null) {
                continue;
                }

                LocalDate billingDate = record.getBillingDate();

                // Include only records from the current month.
                if (billingDate.isBefore(monthStart) ||
                        billingDate.isAfter(today)) {
                continue;
                }

                String recordType = record.getRecordType();

                if (recordType == null ||
                        !recordType.trim().equalsIgnoreCase("Usage")) {
                continue;
                }

                BigDecimal cost = record.getUnblendedCost();

                // Gross usage includes positive usage charges.
                if (cost.signum() > 0) {
                monthlyGrossUsage =
                        monthlyGrossUsage.add(cost);
                }
        }

        return monthlyGrossUsage;
        }

    public List<ServiceCostResponse> getServiceCostBreakdown() {

        List<CostRecord> records =
                costRecordRepository.findAll();

        Map<String, BigDecimal> serviceCosts =
                new LinkedHashMap<>();

        for (CostRecord record : records) {

            String serviceName = record.getServiceName();

            if (serviceName == null ||
                    serviceName.isBlank()) {
                serviceName = "Unknown";
            }

            BigDecimal cost = record.getUnblendedCost();

            if (cost == null) {
                continue;
            }

            serviceCosts.merge(
                    serviceName,
                    cost,
                    BigDecimal::add
            );
        }

        String currency = records.stream()
                .map(CostRecord::getCurrency)
                .filter(value ->
                        value != null && !value.isBlank())
                .findFirst()
                .orElse("USD");

        return serviceCosts.entrySet()
                .stream()
                .map(entry -> new ServiceCostResponse(
                        entry.getKey(),
                        entry.getValue(),
                        currency
                ))
                .toList();
    }

    private CostRecordResponse toResponse(
            CostRecord costRecord
    ) {

        return new CostRecordResponse(
                costRecord.getId(),
                costRecord.getBillingDate(),
                costRecord.getServiceName(),
                costRecord.getRegion(),
                costRecord.getUsageType(),
                costRecord.getUsageQuantity(),
                costRecord.getUsageUnit(),
                costRecord.getUnblendedCost(),
                costRecord.getBlendedCost(),
                costRecord.getCurrency()
        );
    }

 public List<CostTrendResponse> getCostTrend() {

    List<CostRecord> records =
            costRecordRepository.findAll();

    Map<LocalDate, BigDecimal> dailyCosts =
            new LinkedHashMap<>();

    for (CostRecord record : records) {

        if (record.getBillingDate() == null) {
            continue;
        }

        if (record.getUnblendedCost() == null) {
            continue;
        }

        String recordType = record.getRecordType();

        // Include only usage charges in the gross usage trend.
        if (recordType == null ||
                !recordType.equalsIgnoreCase("Usage")) {
            continue;
        }

        BigDecimal cost = record.getUnblendedCost();

        // Exclude negative adjustments from gross usage.
        if (cost.signum() < 0) {
            continue;
        }

        dailyCosts.merge(
                record.getBillingDate(),
                cost,
                BigDecimal::add
        );
    }

    String currency = records.stream()
            .map(CostRecord::getCurrency)
            .filter(value ->
                    value != null && !value.isBlank())
            .findFirst()
            .orElse("USD");

    return dailyCosts.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> new CostTrendResponse(
                    entry.getKey(),
                    entry.getValue(),
                    currency
            ))
            .toList();
}
}