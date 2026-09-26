package com.costpilot.aws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

@Configuration
public class AwsConfig {

    @Bean
    public ProfileCredentialsProvider awsCredentialsProvider() {
        return ProfileCredentialsProvider.builder()
                .profileName("costpilot")
                .build();
    }

    @Bean
    public Ec2Client ec2Client(ProfileCredentialsProvider awsCredentialsProvider) {
        return Ec2Client.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }
}