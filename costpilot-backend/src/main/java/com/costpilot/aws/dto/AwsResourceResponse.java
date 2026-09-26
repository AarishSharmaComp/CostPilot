package com.costpilot.aws.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AwsResourceResponse {

    private UUID id;
    private String resourceId;
    private String resourceArn;
    private String resourceName;
    private String serviceName;
    private String resourceType;
    private String environment;
    private String status;
    private String instanceType;
    private String availabilityZone;
    private Boolean terraformManaged;
    private String terraformAddress;
    private LocalDateTime launchTime;
    private LocalDateTime discoveredAt;
    private LocalDateTime updatedAt;

    public AwsResourceResponse() {
    }

    public AwsResourceResponse(
            UUID id,
            String resourceId,
            String resourceArn,
            String resourceName,
            String serviceName,
            String resourceType,
            String environment,
            String status,
            String instanceType,
            String availabilityZone,
            Boolean terraformManaged,
            String terraformAddress,
            LocalDateTime launchTime,
            LocalDateTime discoveredAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.resourceId = resourceId;
        this.resourceArn = resourceArn;
        this.resourceName = resourceName;
        this.serviceName = serviceName;
        this.resourceType = resourceType;
        this.environment = environment;
        this.status = status;
        this.instanceType = instanceType;
        this.availabilityZone = availabilityZone;
        this.terraformManaged = terraformManaged;
        this.terraformAddress = terraformAddress;
        this.launchTime = launchTime;
        this.discoveredAt = discoveredAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceArn() {
        return resourceArn;
    }

    public void setResourceArn(String resourceArn) {
        this.resourceArn = resourceArn;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getAvailabilityZone() {
        return availabilityZone;
    }

    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    public Boolean getTerraformManaged() {
        return terraformManaged;
    }

    public void setTerraformManaged(Boolean terraformManaged) {
        this.terraformManaged = terraformManaged;
    }

    public String getTerraformAddress() {
        return terraformAddress;
    }

    public void setTerraformAddress(String terraformAddress) {
        this.terraformAddress = terraformAddress;
    }

    public LocalDateTime getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(LocalDateTime launchTime) {
        this.launchTime = launchTime;
    }

    public LocalDateTime getDiscoveredAt() {
        return discoveredAt;
    }

    public void setDiscoveredAt(LocalDateTime discoveredAt) {
        this.discoveredAt = discoveredAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}