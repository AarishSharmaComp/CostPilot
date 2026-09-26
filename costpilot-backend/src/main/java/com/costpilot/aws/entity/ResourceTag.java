package com.costpilot.aws.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "resource_tags",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_resource_tag_key",
                        columnNames = {"resource_id", "key"}
                )
        }
)
public class ResourceTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private AwsResource resource;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "value")
    private String value;

    public ResourceTag() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AwsResource getResource() {
        return resource;
    }

    public void setResource(AwsResource resource) {
        this.resource = resource;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}