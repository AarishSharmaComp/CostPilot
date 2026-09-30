package com.costpilot.aws.service;

import com.costpilot.aws.entity.AwsRegion;
import com.costpilot.aws.repository.AwsRegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeRegionsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeRegionsResponse;
import software.amazon.awssdk.services.ec2.model.Region;

import java.util.List;

@Service
public class AwsRegionService {

    private final Ec2Client ec2Client;
    private final AwsRegionRepository awsRegionRepository;

    public AwsRegionService(
            Ec2Client ec2Client,
            AwsRegionRepository awsRegionRepository
    ) {
        this.ec2Client = ec2Client;
        this.awsRegionRepository = awsRegionRepository;
    }

    @Transactional
    public int syncRegions() {

        DescribeRegionsRequest request =
                DescribeRegionsRequest.builder()
                        .allRegions(false)
                        .build();

        DescribeRegionsResponse response =
                ec2Client.describeRegions(request);

        int synchronizedCount = 0;

        for (Region awsRegion : response.regions()) {

            String regionCode = awsRegion.regionName();

            AwsRegion region = awsRegionRepository
                    .findByCode(regionCode)
                    .orElseGet(AwsRegion::new);

            region.setCode(regionCode);
            region.setName(regionCode);
            region.setActive(true);

            awsRegionRepository.save(region);

            synchronizedCount++;
        }

        return synchronizedCount;
    }
}