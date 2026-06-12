package com.turnerochubut.api.catalog;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {

    boolean existsByCodeIgnoreCase(String code);

    Optional<CatalogItem> findByCodeIgnoreCase(String code);
}
