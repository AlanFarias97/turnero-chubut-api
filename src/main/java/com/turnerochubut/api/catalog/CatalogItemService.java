package com.turnerochubut.api.catalog;

import java.util.List;
import java.util.Locale;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
class CatalogItemService {

    private final CatalogItemRepository catalogItemRepository;

    CatalogItemService(CatalogItemRepository catalogItemRepository) {
        this.catalogItemRepository = catalogItemRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "catalogItems", key = "'all'")
    List<CatalogItemResponse> listItems() {
        return catalogItemRepository
            .findAll(Sort.by(Sort.Direction.ASC, "code"))
            .stream()
            .map(CatalogItemResponse::from)
            .toList();
    }

    @Transactional
    @CacheEvict(cacheNames = "catalogItems", allEntries = true)
    CatalogItemResponse createItem(CatalogItemRequest request) {
        String code = normalizeCode(request.code());
        if (catalogItemRepository.existsByCodeIgnoreCase(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un item con ese codigo");
        }

        CatalogItem item = new CatalogItem(
            code,
            request.name().trim(),
            request.category(),
            request.active(),
            normalizeOptional(request.sourceRubro())
        );

        return CatalogItemResponse.from(catalogItemRepository.save(item));
    }

    @Transactional
    @CacheEvict(cacheNames = "catalogItems", allEntries = true)
    CatalogItemResponse updateItem(Long id, CatalogItemRequest request) {
        CatalogItem item = findItem(id);
        String code = normalizeCode(request.code());

        catalogItemRepository
            .findByCodeIgnoreCase(code)
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(_existing -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un item con ese codigo");
            });

        item.update(
            code,
            request.name().trim(),
            request.category(),
            request.active(),
            normalizeOptional(request.sourceRubro())
        );

        return CatalogItemResponse.from(item);
    }

    @Transactional
    @CacheEvict(cacheNames = "catalogItems", allEntries = true)
    CatalogItemResponse setActive(Long id, boolean active) {
        CatalogItem item = findItem(id);
        item.setActive(active);
        return CatalogItemResponse.from(item);
    }

    @Transactional
    @CacheEvict(cacheNames = "catalogItems", allEntries = true)
    void deleteItem(Long id) {
        CatalogItem item = findItem(id);
        catalogItemRepository.delete(item);
    }

    private CatalogItem findItem(Long id) {
        return catalogItemRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de catalogo no encontrado"));
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeOptional(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }
}
