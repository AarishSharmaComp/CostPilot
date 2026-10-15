package com.costpilot.cost.repository;

import com.costpilot.cost.entity.CostRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CostRecordRepository
        extends JpaRepository<CostRecord, Long> {

    List<CostRecord> findByAwsAccount_Id(UUID awsAccountId);

    List<CostRecord> findByAwsAccount_IdAndBillingDateBetween(
            UUID awsAccountId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM CostRecord c
        WHERE c.awsAccount.id = :accountId
          AND c.billingDate >= :startDate
          AND c.billingDate < :endDate
          AND c.resource IS NULL
        """)
    int deleteSyncedCostRecords(
            @Param("accountId") UUID accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}