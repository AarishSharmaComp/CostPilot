package com.costpilot.cost.repository;

import com.costpilot.cost.entity.CostRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CostRecordRepository extends JpaRepository<CostRecord, Long> {

    List<CostRecord> findByAwsAccount_Id(UUID awsAccountId);

    List<CostRecord> findByAwsAccount_IdAndBillingDateBetween(
            UUID awsAccountId,
            LocalDate startDate,
            LocalDate endDate
    );
}