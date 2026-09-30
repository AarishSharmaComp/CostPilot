package com.costpilot.aws.service;

import com.costpilot.aws.entity.AwsAccount;
import com.costpilot.aws.entity.AwsRegion;
import com.costpilot.aws.entity.AwsResource;
import com.costpilot.aws.entity.ResourceTag;
import com.costpilot.aws.repository.AwsAccountRepository;
import com.costpilot.aws.repository.AwsRegionRepository;
import com.costpilot.aws.repository.AwsResourceRepository;
import com.costpilot.aws.repository.ResourceTagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.Instance;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class AwsResourceSyncService {

    private final AwsAccountRepository awsAccountRepository;
    private final AwsRegionRepository awsRegionRepository;
    private final AwsResourceRepository awsResourceRepository;
    private final ResourceTagRepository resourceTagRepository;
    private final AwsEc2ClientFactory awsEc2ClientFactory;

    public AwsResourceSyncService(
            AwsAccountRepository awsAccountRepository,
            AwsRegionRepository awsRegionRepository,
            AwsResourceRepository awsResourceRepository,
            ResourceTagRepository resourceTagRepository,
            AwsEc2ClientFactory awsEc2ClientFactory
    ) {
        this.awsAccountRepository = awsAccountRepository;
        this.awsRegionRepository = awsRegionRepository;
        this.awsResourceRepository = awsResourceRepository;
        this.resourceTagRepository = resourceTagRepository;
        this.awsEc2ClientFactory = awsEc2ClientFactory;
    }

    @Transactional
    public int syncEc2Resources(UUID awsAccountId) {

        AwsAccount awsAccount = awsAccountRepository.findById(awsAccountId)
                .orElseThrow(() -> new RuntimeException("AWS account not found"));

        var activeRegions = awsRegionRepository.findByActiveTrue();

        if (activeRegions.isEmpty()) {
            throw new RuntimeException("No active AWS regions found");
        }

        int synchronizedCount = 0;

        for (AwsRegion awsRegion : activeRegions) {

            String regionCode = awsRegion.getCode();

            Ec2Client ec2Client =
                    awsEc2ClientFactory.createClient(regionCode);

            try {

                DescribeInstancesRequest request =
                        DescribeInstancesRequest.builder()
                                .build();

                var paginator =
                        ec2Client.describeInstancesPaginator(request);

                for (var response : paginator) {

                    for (var reservation : response.reservations()) {

                        for (Instance instance : reservation.instances()) {

                            AwsResource resource =
                                    awsResourceRepository
                                            .findByAwsAccount_IdAndResourceId(
                                                    awsAccountId,
                                                    instance.instanceId()
                                            )
                                            .orElseGet(AwsResource::new);

                            resource.setAwsAccount(awsAccount);
                            resource.setRegion(awsRegion);

                            resource.setResourceId(
                                    instance.instanceId()
                            );

                            resource.setResourceArn(
                                    "arn:aws:ec2:"
                                            + regionCode
                                            + ":"
                                            + awsAccount.getAccountId()
                                            + ":instance/"
                                            + instance.instanceId()
                            );

                            resource.setResourceName(
                                    instance.tags()
                                            .stream()
                                            .filter(tag ->
                                                    "Name".equals(tag.key()))
                                            .map(tag -> tag.value())
                                            .findFirst()
                                            .orElse(null)
                            );

                            resource.setServiceName("EC2");

                            resource.setResourceType("INSTANCE");

                            resource.setStatus(
                                    instance.state() != null
                                            ? instance.state().nameAsString()
                                            : null
                            );

                            resource.setInstanceType(
                                    instance.instanceTypeAsString()
                            );

                            if (instance.placement() != null) {

                                resource.setAvailabilityZone(
                                        instance.placement()
                                                .availabilityZone()
                                );
                            }

                            resource.setTerraformManaged(false);

                            resource.setTerraformAddress(null);

                            if (instance.launchTime() != null) {

                                resource.setLaunchTime(
                                        LocalDateTime.ofInstant(
                                                instance.launchTime(),
                                                ZoneOffset.UTC
                                        )
                                );
                            }

                            resource.setMetadata("{}");

                            LocalDateTime now =
                                    LocalDateTime.now();

                            if (resource.getDiscoveredAt() == null) {

                                resource.setDiscoveredAt(now);
                            }

                            resource.setUpdatedAt(now);

                            AwsResource savedResource =
                                    awsResourceRepository.save(resource);

                            resourceTagRepository
                                    .deleteByResource_Id(
                                            savedResource.getId()
                                    );

                            instance.tags().forEach(tag -> {

                                ResourceTag resourceTag =
                                        new ResourceTag();

                                resourceTag.setResource(
                                        savedResource
                                );

                                resourceTag.setKey(
                                        tag.key()
                                );

                                resourceTag.setValue(
                                        tag.value()
                                );

                                resourceTagRepository.save(
                                        resourceTag
                                );
                            });

                            synchronizedCount++;
                        }
                    }
                }

            } finally {

                ec2Client.close();
            }
        }

        LocalDateTime now = LocalDateTime.now();

        awsAccount.setLastSyncedAt(now);
        awsAccount.setUpdatedAt(now);

        awsAccountRepository.save(awsAccount);

        return synchronizedCount;
    }
}