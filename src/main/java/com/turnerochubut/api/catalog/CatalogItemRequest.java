package com.turnerochubut.api.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

record CatalogItemRequest(
    @NotBlank
    @Size(max = 40)
    String code,

    @NotBlank
    @Size(max = 180)
    String name,

    @NotNull
    CatalogCategory category,

    boolean active,

    @Size(max = 120)
    String sourceRubro
) {
}
