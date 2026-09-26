package com.costpilot.cost.service;

import com.costpilot.cost.dto.CostRecordResponse;
import com.costpilot.cost.entity.CostRecord;
import com.costpilot.cost.repository.CostRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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
}