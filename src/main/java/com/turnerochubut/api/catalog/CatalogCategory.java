package com.turnerochubut.api.catalog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

enum CatalogCategory {
    SERVICE("service"),
    REPAIR("repair");

    private final String value;

    CatalogCategory(String value) {
        this.value = value;
    }

    @JsonCreator
    static CatalogCategory from(String value) {
        return Arrays.stream(values())
            .filter(category -> category.value.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Categoria de catalogo invalida"));
    }

    @JsonValue
    String value() {
        return value;
    }
}
