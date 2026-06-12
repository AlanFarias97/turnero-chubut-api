package com.turnerochubut.api.catalog;

record CatalogItemResponse(
    Long id,
    String code,
    String name,
    CatalogCategory category,
    boolean active,
    String sourceRubro
) {

    static CatalogItemResponse from(CatalogItem item) {
        return new CatalogItemResponse(
            item.getId(),
            item.getCode(),
            item.getName(),
            item.getCategory(),
            item.isActive(),
            item.getSourceRubro()
        );
    }
}
