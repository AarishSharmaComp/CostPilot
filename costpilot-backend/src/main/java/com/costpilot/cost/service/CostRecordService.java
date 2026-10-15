package com.costpilot.cost.service;

import com.costpilot.aws.entity.AwsAccount;
import com.costpilot.aws.repository.AwsAccountRepository;
import com.costpilot.cost.entity.CostRecord;
import com.costpilot.cost.repository.CostRecordRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.DateInterval;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageRequest;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.Group;
import software.amazon.awssdk.services.costexplorer.model.GroupDefinition;
import software.amazon.awssdk.services.costexplorer.model.GroupDefinitionType;
import software.amazon.awssdk.services.costexplorer.model.MetricValue;
import software.amazon.awssdk.services.costexplorer.model.ResultByTime;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CostRecordService {

    private final CostExplorerClient costExplorerClient;
    private final CostRecordRepository costRecordRepository;
    private final AwsAccountRepository awsAccountRepository;

    public CostRecordService(
            CostExplorerClient costExplorerClient,
            CostRecordRepository costRecordRepository,
            AwsAccountRepository awsAccountRepository
    ) {
        this.costExplorerClient = costExplorerClient;
        this.costRecordRepository = costRecordRepository;
        this.awsAccountRepository = awsAccountRepository;
    }

    @Transactional
    public int syncCosts(UUID awsAccountId) {

        AwsAccount awsAccount = awsAccountRepository.findById(awsAccountId)
                .orElseThrow(() ->
                        new RuntimeException("AWS account not found"));

        /*
         * Fetch the previous six complete months plus the current
         * month-to-date. Cost Explorer's end date is exclusive.
         */
        LocalDate today = LocalDate.now();
        LocalDate endDate = today;
        LocalDate startDate = today.withDayOfMonth(1).minusMonths(5);

        DateInterval timePeriod = DateInterval.builder()
                .start(startDate.toString())
                .end(endDate.toString())
                .build();

        GroupDefinition serviceGroup = GroupDefinition.builder()
                .type(GroupDefinitionType.DIMENSION)
                .key("SERVICE")
                .build();

        GroupDefinition recordTypeGroup = GroupDefinition.builder()
                .type(GroupDefinitionType.DIMENSION)
                .key("RECORD_TYPE")
                .build();

        GetCostAndUsageRequest request = GetCostAndUsageRequest.builder()
                .timePeriod(timePeriod)
                .granularity("DAILY")
                .metrics("UnblendedCost")
                .groupBy(serviceGroup, recordTypeGroup)
                .build();

        GetCostAndUsageResponse response =
                costExplorerClient.getCostAndUsage(request);

        /*
         * Replace only previously synchronized aggregate records
         * for this account and reporting period.
         *
         * Resource-linked records are preserved by the repository
         * deletion query.
         */
        costRecordRepository.deleteSyncedCostRecords(
                awsAccountId,
                startDate,
                endDate
        );

        int savedCount = 0;

        for (ResultByTime result : response.resultsByTime()) {

            LocalDate billingDate =
                    LocalDate.parse(result.timePeriod().start());

            for (Group group : result.groups()) {

                MetricValue cost =
                        group.metrics().get("UnblendedCost");

                if (cost == null || cost.amount() == null) {
                    continue;
                }

                List<String> keys = group.keys();

                String serviceName =
                        keys.size() > 0 && !keys.get(0).isBlank()
                                ? keys.get(0)
                                : "Unknown";

                String recordType =
                        keys.size() > 1 && !keys.get(1).isBlank()
                                ? keys.get(1)
                                : "Unknown";

                CostRecord costRecord = new CostRecord();

                costRecord.setAwsAccount(awsAccount);
                costRecord.setServiceName(serviceName);
                costRecord.setRecordType(recordType);
                costRecord.setBillingDate(billingDate);
                costRecord.setUnblendedCost(
                        new BigDecimal(cost.amount())
                );
                costRecord.setCurrency(cost.unit());
                costRecord.setCreatedAt(LocalDateTime.now());

                costRecordRepository.save(costRecord);
                savedCount++;
            }
        }

        return savedCount;
    }
}