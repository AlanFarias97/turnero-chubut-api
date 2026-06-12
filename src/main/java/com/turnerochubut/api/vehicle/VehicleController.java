package com.turnerochubut.api.vehicle;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicles")
class VehicleController {

    private final VehicleService vehicleService;

    VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/dashboard")
    DashboardStateResponse getDashboardState() {
        return vehicleService.getDashboardState();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    VehicleResponse createVehicle(@Valid @RequestBody VehicleRequest request) {
        return vehicleService.createVehicle(request);
    }

    @PutMapping("/{id}")
    VehicleResponse updateVehicle(
        @PathVariable Long id,
        @Valid @RequestBody VehicleRequest request
    ) {
        return vehicleService.updateVehicle(id, request);
    }

    @PostMapping("/{id}/assign-to-bay")
    DashboardStateResponse assignToBay(
        @PathVariable Long id,
        @Valid @RequestBody AssignVehicleRequest request
    ) {
        return vehicleService.assignToBay(id, request);
    }

    @PostMapping("/{id}/move-to-queue")
    DashboardStateResponse moveToQueue(@PathVariable Long id) {
        return vehicleService.moveToQueue(id);
    }

    @PostMapping("/{id}/complete")
    DashboardStateResponse completeVehicle(
        @PathVariable Long id,
        @Valid @RequestBody CompleteVehicleRequest request
    ) {
        return vehicleService.completeVehicle(id, request);
    }
}
