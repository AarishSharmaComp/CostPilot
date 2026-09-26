package com.costpilot.aws.dto;

public class Ec2InstanceResponse {

    private String instanceId;
    private String instanceType;
    private String state;
    private String availabilityZone;
    private String privateIpAddress;
    private String publicIpAddress;
    private String platform;
    private String name;

    public Ec2InstanceResponse() {
    }

    public Ec2InstanceResponse(
            String instanceId,
            String instanceType,
            String state,
            String availabilityZone,
            String privateIpAddress,
            String publicIpAddress,
            String platform,
            String name
    ) {
        this.instanceId = instanceId;
        this.instanceType = instanceType;
        this.state = state;
        this.availabilityZone = availabilityZone;
        this.privateIpAddress = privateIpAddress;
        this.publicIpAddress = publicIpAddress;
        this.platform = platform;
        this.name = name;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAvailabilityZone() {
        return availabilityZone;
    }

    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

    public String getPublicIpAddress() {
        return publicIpAddress;
    }

    public void setPublicIpAddress(String publicIpAddress) {
        this.publicIpAddress = publicIpAddress;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}