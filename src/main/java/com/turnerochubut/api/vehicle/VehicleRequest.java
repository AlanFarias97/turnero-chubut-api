package com.turnerochubut.api.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

record VehicleRequest(
    @NotBlank
    @Size(max = 30)
    String patent,

    @NotBlank
    @Size(max = 255)
    String description,

    @NotBlank
    String service,

    @NotNull
    VehiclePaymentStatus paymentStatus,

    List<@Size(max = 80) String> assignedOperators
) {
}
