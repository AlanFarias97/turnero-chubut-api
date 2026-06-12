package com.turnerochubut.api.vehicle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

enum VehiclePaymentStatus {
    UNPAID("unpaid"),
    PARTIAL("partial"),
    PAID("paid");

    private final String value;

    VehiclePaymentStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    static VehiclePaymentStatus fromJson(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        for (VehiclePaymentStatus status : values()) {
            if (status.value.equals(normalized) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Estado de pago invalido: " + value);
    }

    @JsonValue
    String toJson() {
        return value;
    }
}
