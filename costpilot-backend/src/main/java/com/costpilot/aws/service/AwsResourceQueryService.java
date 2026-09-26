package com.costpilot.aws.service;

import com.costpilot.aws.dto.AwsResourceResponse;
import com.costpilot.aws.entity.AwsResource;
import com.costpilot.aws.repository.AwsResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AwsResourceQueryService {

    private final AwsResourceRepository awsResourceRepository;

    public AwsResourceQueryService(
            AwsResourceRepository awsResourceRepository
    ) {
        this.awsResourceRepository = awsResourceRepository;
    }

    public List<AwsResourceResponse> getAllResources() {
        return awsResourceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AwsResourceResponse> getResourcesByAccount(
            UUID awsAccountId
    ) {
        return awsResourceRepository
                .findByAwsAccount_Id(awsAccountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AwsResourceResponse toResponse(AwsResource resource) {

        return new AwsResourceResponse(
                resource.getId(),
                resource.getResourceId(),
                resource.getResourceArn(),
                resource.getResourceName(),
                resource.getServiceName(),
                resource.getResourceType(),
                resource.getEnvironment(),
                resource.getStatus(),
                resource.getInstanceType(),
                resource.getAvailabilityZone(),
                resource.getTerraformManaged(),
                resource.getTerraformAddress(),
                resource.getLaunchTime(),
                resource.getDiscoveredAt(),
                resource.getUpdatedAt()
        );
    }
}