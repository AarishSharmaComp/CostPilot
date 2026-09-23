package com.costpilot.aws.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateAwsAccountRequest {

    @NotBlank
    private String accountId;

    @NotBlank
    private String name;

    private String roleArn;

    private String status;

    private String defaultRegion;

    public CreateAwsAccountRequest() {
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleArn() {
        return roleArn;
    }

    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDefaultRegion() {
        return defaultRegion;
    }

    public void setDefaultRegion(String defaultRegion) {
        this.defaultRegion = defaultRegion;
    }
}