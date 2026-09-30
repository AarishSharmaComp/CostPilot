package com.costpilot.aws.service;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

@Component
public class AwsEc2ClientFactory {

    private final ProfileCredentialsProvider credentialsProvider;

    public AwsEc2ClientFactory(
            ProfileCredentialsProvider credentialsProvider
    ) {
        this.credentialsProvider = credentialsProvider;
    }

    public Ec2Client createClient(String regionCode) {

        return Ec2Client.builder()
                .region(Region.of(regionCode))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}