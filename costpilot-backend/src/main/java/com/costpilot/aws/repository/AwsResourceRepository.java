package com.costpilot.aws.repository;

import com.costpilot.aws.entity.AwsResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AwsResourceRepository extends JpaRepository<AwsResource, UUID> {

    List<AwsResource> findByAwsAccount_Id(UUID awsAccountId);

    Optional<AwsResource> findByAwsAccount_IdAndResourceId(
            UUID awsAccountId,
            String resourceId
    );

    boolean existsByAwsAccount_IdAndResourceId(
            UUID awsAccountId,
            String resourceId
    );
}