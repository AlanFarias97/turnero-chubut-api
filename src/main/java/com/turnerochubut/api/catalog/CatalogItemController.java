package com.turnerochubut.api.catalog;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogs")
class CatalogItemController {

    private final CatalogItemService catalogItemService;

    CatalogItemController(CatalogItemService catalogItemService) {
        this.catalogItemService = catalogItemService;
    }

    @GetMapping
    List<CatalogItemResponse> listItems() {
        return catalogItemService.listItems();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CatalogItemResponse createItem(@Valid @RequestBody CatalogItemRequest request) {
        return catalogItemService.createItem(request);
    }

    @PutMapping("/{id}")
    CatalogItemResponse updateItem(
        @PathVariable Long id,
        @Valid @RequestBody CatalogItemRequest request
    ) {
        return catalogItemService.updateItem(id, request);
    }

    @PatchMapping("/{id}/active")
    CatalogItemResponse setActive(
        @PathVariable Long id,
        @RequestBody SetCatalogItemActiveRequest request
    ) {
        return catalogItemService.setActive(id, request.active());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    void deleteItem(@PathVariable Long id) {
        catalogItemService.deleteItem(id);
    }
}
