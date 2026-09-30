package com.costpilot.aws.repository;

import com.costpilot.aws.entity.AwsRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AwsRegionRepository extends JpaRepository<AwsRegion, Integer> {

    Optional<AwsRegion> findByCode(String code);

    boolean existsByCode(String code);

    List<AwsRegion> findByActiveTrue();
}