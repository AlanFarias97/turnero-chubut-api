package com.turnerochubut.api.vehicle;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

record AssignVehicleRequest(
    @NotNull
    @Min(1)
    @Max(3)
    Integer bayId,

    List<@Size(max = 80) String> assignedOperators
) {
}
