package com.costpilot.aws.service;

import com.costpilot.aws.entity.AwsResource;
import com.costpilot.aws.entity.ResourceMetric;
import com.costpilot.aws.repository.AwsResourceRepository;
import com.costpilot.aws.repository.ResourceMetricRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.Metric;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataQuery;
import software.amazon.awssdk.services.cloudwatch.model.MetricStat;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class CloudWatchMetricService {

    private final CloudWatchClient cloudWatchClient;
    private final AwsResourceRepository awsResourceRepository;
    private final ResourceMetricRepository resourceMetricRepository;

    public CloudWatchMetricService(
            CloudWatchClient cloudWatchClient,
            AwsResourceRepository awsResourceRepository,
            ResourceMetricRepository resourceMetricRepository
    ) {
        this.cloudWatchClient = cloudWatchClient;
        this.awsResourceRepository = awsResourceRepository;
        this.resourceMetricRepository = resourceMetricRepository;
    }

    @Transactional
    public int syncCpuMetrics(UUID resourceId) {

        AwsResource resource = awsResourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("AWS resource not found"));

        if (!"EC2".equalsIgnoreCase(resource.getServiceName())) {
            throw new RuntimeException("Resource is not an EC2 resource");
        }

        Instant endTime = Instant.now();
        Instant startTime = endTime.minusSeconds(3600);

        Dimension instanceDimension = Dimension.builder()
                .name("InstanceId")
                .value(resource.getResourceId())
                .build();

        Metric metric = Metric.builder()
                .namespace("AWS/EC2")
                .metricName("CPUUtilization")
                .dimensions(instanceDimension)
                .build();

        MetricStat metricStat = MetricStat.builder()
                .metric(metric)
                .period(300)
                .stat(Statistic.AVERAGE.toString())
                .build();

        MetricDataQuery query = MetricDataQuery.builder()
                .id("cpuutilization")
                .metricStat(metricStat)
                .returnData(true)
                .build();

        GetMetricDataRequest request = GetMetricDataRequest.builder()
                .startTime(startTime)
                .endTime(endTime)
                .metricDataQueries(query)
                .scanBy("TimestampDescending")
                .build();

        GetMetricDataResponse response =
                cloudWatchClient.getMetricData(request);

        if (response.metricDataResults().isEmpty()) {
            return 0;
        }

        var result = response.metricDataResults().get(0);

        List<Instant> timestamps = result.timestamps();
        List<Double> values = result.values();

        int savedCount = 0;

        for (int i = 0; i < timestamps.size(); i++) {

            if (i >= values.size()) {
                continue;
            }

            Double value = values.get(i);

            if (value == null) {
                continue;
            }

            ResourceMetric resourceMetric = new ResourceMetric();

            resourceMetric.setResource(resource);
            resourceMetric.setMetricName("CPUUtilization");
            resourceMetric.setMetricNamespace("AWS/EC2");

            resourceMetric.setTimestamp(
                    LocalDateTime.ofInstant(
                            timestamps.get(i),
                            ZoneOffset.UTC
                    )
            );

            resourceMetric.setValue(value);

            resourceMetric.setUnit("Percent");
            resourceMetric.setStatistic("Average");
            resourceMetric.setCreatedAt(LocalDateTime.now());

            resourceMetricRepository.save(resourceMetric);

            savedCount++;
        }

        return savedCount;
    }
}