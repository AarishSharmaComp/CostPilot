package com.costpilot.aws.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "aws_resources",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_aws_resource_account_resource",
                        columnNames = {"aws_account_id", "resource_id"}
                )
        }
)
public class AwsResource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aws_account_id", nullable = false)
    private AwsAccount awsAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private AwsRegion region;

    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Column(name = "resource_arn")
    private String resourceArn;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "resource_type", nullable = false)
    private String resourceType;

    @Column(name = "environment")
    private String environment;

    @Column(name = "status")
    private String status;

    @Column(name = "instance_type")
    private String instanceType;

    @Column(name = "availability_zone")
    private String availabilityZone;

    @Column(name = "terraform_managed", nullable = false)
    private Boolean terraformManaged;

    @Column(name = "terraform_address")
    private String terraformAddress;

    @Column(name = "launch_time")
    private LocalDateTime launchTime;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "discovered_at", nullable = false)
    private LocalDateTime discoveredAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AwsResource() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AwsAccount getAwsAccount() {
        return awsAccount;
    }

    public void setAwsAccount(AwsAccount awsAccount) {
        this.awsAccount = awsAccount;
    }

    public AwsRegion getRegion() {
        return region;
    }

    public void setRegion(AwsRegion region) {
        this.region = region;
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

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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