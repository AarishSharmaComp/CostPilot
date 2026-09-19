package com.costpilot.aws.repository;

import com.costpilot.aws.entity.AccountUser;
import com.costpilot.aws.entity.AccountUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountUserRepository
        extends JpaRepository<AccountUser, AccountUserId> {

    List<AccountUser> findById_UserId(UUID userId);

    List<AccountUser> findById_AwsAccountId(UUID awsAccountId);
}