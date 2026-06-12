package com.turnerochubut.api.vehicle;

record BayResponse(
    int id,
    String name,
    VehicleResponse currentVehicle
) {
}
