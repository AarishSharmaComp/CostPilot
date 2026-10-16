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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

                if (cost.signum() > 0) {
                    grossUsage = grossUsage.add(cost);
                } else if (cost.signum() < 0) {
                    otherAdjustments =
                            otherAdjustments.add(cost);
                }

            } else if (recordType.equalsIgnoreCase("Credit")) {

                credits = credits.add(cost.abs());

            } else if (recordType.equalsIgnoreCase("Tax")) {

                tax = tax.add(cost);

            } else {

                otherAdjustments =
                        otherAdjustments.add(cost);
            }
        }

        BigDecimal netCost = grossUsage
                .subtract(credits)
                .add(tax)
                .add(otherAdjustments);

        String currency = getCurrency(records);

        return new CostSummaryResponse(
                grossUsage,
                credits,
                tax,
                netCost,
                currency,
                records.size()
        );
    }

    /**
     * Returns positive AWS Usage charges for the current
     * calendar month, excluding credits and taxes.
     */
    public BigDecimal getMonthlyGrossUsage() {

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        List<CostRecord> records =
                costRecordRepository.findByBillingDateBetween(
                        monthStart,
                        today
                );

        return calculateGrossUsage(records);
    }

    /**
     * Returns positive AWS Usage charges for the requested
     * number of days, including today.
     */
    public BigDecimal getGrossUsage(int days) {

        validateDays(days);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate =
                endDate.minusDays(days - 1L);

        List<CostRecord> records =
                costRecordRepository.findByBillingDateBetween(
                        startDate,
                        endDate
                );

        return calculateGrossUsage(records);
    }

    /**
     * Returns daily AWS Usage charges for the requested
     * period. Dates without records are returned as zero.
     */
    public List<CostTrendResponse> getCostTrend(int days) {

        validateDays(days);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate =
                endDate.minusDays(days - 1L);

        List<CostRecord> records =
                costRecordRepository.findByBillingDateBetween(
                        startDate,
                        endDate
                );

        Map<LocalDate, BigDecimal> dailyCosts =
                new TreeMap<>();

        for (CostRecord record : records) {

            LocalDate billingDate = record.getBillingDate();

            if (billingDate == null) {
                continue;
            }

            if (!isUsageRecord(record)) {
                continue;
            }

            BigDecimal cost = record.getUnblendedCost();

            if (cost == null || cost.signum() <= 0) {
                continue;
            }

            dailyCosts.merge(
                    billingDate,
                    cost,
                    BigDecimal::add
            );
        }

        String currency = getCurrency(records);

        List<CostTrendResponse> result =
                new ArrayList<>();

        for (LocalDate date = startDate;
             !date.isAfter(endDate);
             date = date.plusDays(1)) {

            result.add(
                    new CostTrendResponse(
                            date,
                            dailyCosts.getOrDefault(
                                    date,
                                    BigDecimal.ZERO
                            ),
                            currency
                    )
            );
        }

        return result;
    }

    /**
     * Preserves compatibility with existing callers.
     * Defaults to the last 30 days.
     */
    public List<CostTrendResponse> getCostTrend() {
        return getCostTrend(30);
    }
    /**
 * Returns daily AWS Usage charges for the current calendar
 * month, from the first day of the month through today.
 * Dates without records are returned as zero.
 */
public List<CostTrendResponse> getCurrentMonthCostTrend() {

    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.withDayOfMonth(1);

    List<CostRecord> records =
            costRecordRepository.findByBillingDateBetween(
                    startDate,
                    endDate
            );

    Map<LocalDate, BigDecimal> dailyCosts =
            new TreeMap<>();

    for (CostRecord record : records) {

        LocalDate billingDate = record.getBillingDate();

        if (billingDate == null) {
            continue;
        }

        if (!isUsageRecord(record)) {
            continue;
        }

        BigDecimal cost = record.getUnblendedCost();

        if (cost == null || cost.signum() <= 0) {
            continue;
        }

        dailyCosts.merge(
                billingDate,
                cost,
                BigDecimal::add
        );
    }

    String currency = getCurrency(records);

    List<CostTrendResponse> result = new ArrayList<>();

    for (LocalDate date = startDate;
         !date.isAfter(endDate);
         date = date.plusDays(1)) {

        result.add(
                new CostTrendResponse(
                        date,
                        dailyCosts.getOrDefault(
                                date,
                                BigDecimal.ZERO
                        ),
                        currency
                )
        );
    }

    return result;
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

            if (!isUsageRecord(record)) {
                continue;
            }

            BigDecimal cost = record.getUnblendedCost();

            if (cost == null || cost.signum() <= 0) {
                continue;
            }

            serviceCosts.merge(
                    serviceName,
                    cost,
                    BigDecimal::add
            );
        }

        String currency = getCurrency(records);

        return serviceCosts.entrySet()
                .stream()
                .map(entry -> new ServiceCostResponse(
                        entry.getKey(),
                        entry.getValue(),
                        currency
                ))
                .toList();
    }

    private BigDecimal calculateGrossUsage(
            List<CostRecord> records
    ) {
        BigDecimal total = BigDecimal.ZERO;

        for (CostRecord record : records) {

            if (!isUsageRecord(record)) {
                continue;
            }

            BigDecimal cost = record.getUnblendedCost();

            if (cost != null && cost.signum() > 0) {
                total = total.add(cost);
            }
        }

        return total;
    }

    private boolean isUsageRecord(CostRecord record) {

        String recordType = record.getRecordType();

        return recordType != null
                && recordType.trim()
                        .equalsIgnoreCase("Usage");
    }

    private void validateDays(int days) {

        if (days != 7 && days != 30 && days != 90) {
            throw new IllegalArgumentException(
                    "Days must be 7, 30, or 90"
            );
        }
    }

    private String getCurrency(List<CostRecord> records) {

        return records.stream()
                .map(CostRecord::getCurrency)
                .filter(value ->
                        value != null && !value.isBlank())
                .findFirst()
                .orElse("USD");
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
}