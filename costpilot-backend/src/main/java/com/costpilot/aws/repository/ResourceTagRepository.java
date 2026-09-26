package com.costpilot.aws.repository;

import com.costpilot.aws.entity.ResourceTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResourceTagRepository extends JpaRepository<ResourceTag, UUID> {

    List<ResourceTag> findByResource_Id(UUID resourceId);

    void deleteByResource_Id(UUID resourceId);
}