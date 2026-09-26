package com.costpilot.aws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;

@Configuration
public class CostExplorerConfig {
    @Bean
    public CostExplorerClient costExplorerClient(
            ProfileCredentialsProvider awsCredentialsProvider
    ) {
        return CostExplorerClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }
}