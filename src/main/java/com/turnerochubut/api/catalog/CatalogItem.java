package com.turnerochubut.api.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "catalog_items")
class CatalogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(nullable = false, length = 180)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CatalogCategory category;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "source_rubro", length = 120)
    private String sourceRubro;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected CatalogItem() {
    }

    CatalogItem(
        String code,
        String name,
        CatalogCategory category,
        boolean active,
        String sourceRubro
    ) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.active = active;
        this.sourceRubro = sourceRubro;
    }

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    Long getId() {
        return id;
    }

    String getCode() {
        return code;
    }

    String getName() {
        return name;
    }

    CatalogCategory getCategory() {
        return category;
    }

    boolean isActive() {
        return active;
    }

    String getSourceRubro() {
        return sourceRubro;
    }

    void update(
        String code,
        String name,
        CatalogCategory category,
        boolean active,
        String sourceRubro
    ) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.active = active;
        this.sourceRubro = sourceRubro;
    }

    void setActive(boolean active) {
        this.active = active;
    }
}
