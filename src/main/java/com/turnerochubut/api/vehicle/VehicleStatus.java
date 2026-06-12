package com.turnerochubut.api.vehicle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

enum VehicleStatus {
    IN_QUEUE("in_queue"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    PARTIAL_COMPLETED("partial_completed"),
    NOT_COMPLETED("not_completed");

    private final String value;

    VehicleStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    static VehicleStatus fromJson(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        for (VehicleStatus status : values()) {
            if (status.value.equals(normalized) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Estado de vehiculo invalido: " + value);
    }

    @JsonValue
    String toJson() {
        return value;
    }
}
