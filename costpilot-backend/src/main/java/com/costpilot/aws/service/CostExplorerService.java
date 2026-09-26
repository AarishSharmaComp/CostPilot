package com.costpilot.aws.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.DateInterval;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageRequest;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.Group;
import software.amazon.awssdk.services.costexplorer.model.GroupDefinition;
import software.amazon.awssdk.services.costexplorer.model.GroupDefinitionType;
import software.amazon.awssdk.services.costexplorer.model.MetricValue;
import software.amazon.awssdk.services.costexplorer.model.ResultByTime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CostExplorerService {

    private final CostExplorerClient costExplorerClient;

    public CostExplorerService(CostExplorerClient costExplorerClient) {
        this.costExplorerClient = costExplorerClient;
    }

    public List<String> getRecentCosts() {

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);

        DateInterval timePeriod = DateInterval.builder()
                .start(startDate.toString())
                .end(endDate.toString())
                .build();

        GroupDefinition groupDefinition = GroupDefinition.builder()
                .type(GroupDefinitionType.DIMENSION)
                .key("SERVICE")
                .build();

        GetCostAndUsageRequest request = GetCostAndUsageRequest.builder()
                .timePeriod(timePeriod)
                .granularity("DAILY")
                .metrics("UnblendedCost")
                .groupBy(groupDefinition)
                .build();

        GetCostAndUsageResponse response =
                costExplorerClient.getCostAndUsage(request);

        List<String> results = new ArrayList<>();

        for (ResultByTime result : response.resultsByTime()) {

            String date = result.timePeriod().start();

            for (Group group : result.groups()) {

                MetricValue cost =
                        group.metrics().get("UnblendedCost");

                if (cost != null) {
                    results.add(
                            date
                                    + " | "
                                    + group.keys()
                                    + " | "
                                    + cost.amount()
                                    + " "
                                    + cost.unit()
                    );
                }
            }
        }

        return results;
    }
}