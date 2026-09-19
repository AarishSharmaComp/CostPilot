package com.costpilot.aws.repository;

import com.costpilot.aws.entity.AwsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AwsAccountRepository extends JpaRepository<AwsAccount, UUID> {

    Optional<AwsAccount> findByAccountId(String accountId);

    boolean existsByAccountId(String accountId);
}