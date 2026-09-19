package com.costpilot.aws.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class AccountUserId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "aws_account_id")
    private UUID awsAccountId;

    public AccountUserId() {
    }

    public AccountUserId(UUID userId, UUID awsAccountId) {
        this.userId = userId;
        this.awsAccountId = awsAccountId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getAwsAccountId() {
        return awsAccountId;
    }

    public void setAwsAccountId(UUID awsAccountId) {
        this.awsAccountId = awsAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountUserId that)) return false;

        return userId != null
                && userId.equals(that.userId)
                && awsAccountId != null
                && awsAccountId.equals(that.awsAccountId);
    }

    @Override
    public int hashCode() {
        return 31 * (userId != null ? userId.hashCode() : 0)
                + (awsAccountId != null ? awsAccountId.hashCode() : 0);
    }
}