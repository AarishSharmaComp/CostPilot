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

    public CostQueryService(CostRecordRepository costRecordRepository) {
        this.costRecordRepository = costRecordRepository;
    }

    public List<CostRecordResponse> getAllCosts() {
        return costRecordRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CostRecordResponse> getCostsByAccount(UUID accountId) {
        return costRecordRepository.findByAwsAccount_Id(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CostSummaryResponse getCostSummary() {

        List<CostRecord> records = costRecordRepository.findAll();

        BigDecimal totalCost = records.stream()
                .map(CostRecord::getUnblendedCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String currency = records.stream()
                .map(CostRecord::getCurrency)
                .filter(value -> value != null && !value.isBlank())
                .findFirst()
                .orElse("USD");

        return new CostSummaryResponse(
                totalCost,
                currency,
                records.size()
        );
    }

    public List<ServiceCostResponse> getServiceCostBreakdown() {

        List<CostRecord> records = costRecordRepository.findAll();

        Map<String, BigDecimal> serviceCosts = new LinkedHashMap<>();

        for (CostRecord record : records) {

            String serviceName = record.getServiceName();

            if (serviceName == null || serviceName.isBlank()) {
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
                .filter(value -> value != null && !value.isBlank())
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

    private CostRecordResponse toResponse(CostRecord costRecord) {

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

        BigDecimal cost =
                record.getUnblendedCost();

        if (cost == null) {
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
            .map(entry ->
                    new CostTrendResponse(
                            entry.getKey(),
                            entry.getValue(),
                            currency
                    )
            )
            .toList();
}
}