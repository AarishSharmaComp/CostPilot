package com.costpilot.aws.entity;

import com.costpilot.auth.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_users")
public class AccountUser {

    @EmbeddedId
    private AccountUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("awsAccountId")
    @JoinColumn(name = "aws_account_id", nullable = false)
    private AwsAccount awsAccount;

    @Column(name = "access_role", nullable = false)
    private String accessRole;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AccountUser() {
    }

    public AccountUserId getId() {
        return id;
    }

    public void setId(AccountUserId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AwsAccount getAwsAccount() {
        return awsAccount;
    }

    public void setAwsAccount(AwsAccount awsAccount) {
        this.awsAccount = awsAccount;
    }

    public String getAccessRole() {
        return accessRole;
    }

    public void setAccessRole(String accessRole) {
        this.accessRole = accessRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}