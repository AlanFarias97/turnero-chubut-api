package com.turnerochubut.api.vehicle;

import java.util.List;

record DashboardStateResponse(
    List<BayResponse> bays,
    List<VehicleResponse> waitingVehicles,
    List<VehicleResponse> completedVehicles,
    int nextTicketNumber
) {
}
