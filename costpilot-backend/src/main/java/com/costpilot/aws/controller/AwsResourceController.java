package com.costpilot.aws.controller;

import com.costpilot.aws.dto.Ec2InstanceResponse;
import com.costpilot.aws.service.AwsResourceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/aws/resources")
public class AwsResourceController {

    private final AwsResourceService awsResourceService;

    public AwsResourceController(AwsResourceService awsResourceService) {
        this.awsResourceService = awsResourceService;
    }

    @GetMapping("/ec2")
    public List<Ec2InstanceResponse> getEc2Instances() {
        return awsResourceService.getEc2Instances();
    }
}