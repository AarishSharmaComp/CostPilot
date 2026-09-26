package com.costpilot.aws.service;

import com.costpilot.aws.dto.Ec2InstanceResponse;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;

import java.util.ArrayList;
import java.util.List;

@Service
public class AwsResourceService {

    private final Ec2Client ec2Client;

    public AwsResourceService(Ec2Client ec2Client) {
        this.ec2Client = ec2Client;
    }

    public List<Ec2InstanceResponse> getEc2Instances() {

        DescribeInstancesRequest request =
                DescribeInstancesRequest.builder()
                        .build();

        DescribeInstancesResponse response =
                ec2Client.describeInstances(request);

        List<Ec2InstanceResponse> instances = new ArrayList<>();

        response.reservations().forEach(reservation ->
                reservation.instances().forEach(instance ->
                        instances.add(toResponse(instance))
                )
        );

        return instances;
    }

    private Ec2InstanceResponse toResponse(Instance instance) {

        String name = instance.tags()
                .stream()
                .filter(tag -> "Name".equals(tag.key()))
                .map(tag -> tag.value())
                .findFirst()
                .orElse(null);

        String availabilityZone = null;

        if (instance.placement() != null) {
            availabilityZone = instance.placement().availabilityZone();
        }

        return new Ec2InstanceResponse(
                instance.instanceId(),
                instance.instanceTypeAsString(),
                instance.state() != null
                        ? instance.state().nameAsString()
                        : null,
                availabilityZone,
                instance.privateIpAddress(),
                instance.publicIpAddress(),
                instance.platformAsString(),
                name
        );
    }
}