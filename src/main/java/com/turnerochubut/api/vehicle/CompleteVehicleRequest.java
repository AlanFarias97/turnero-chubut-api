package com.turnerochubut.api.vehicle;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

record CompleteVehicleRequest(
    @NotNull
    VehicleStatus status,

    @Size(max = 2000)
    String pendingWorkDetail
) {
}
