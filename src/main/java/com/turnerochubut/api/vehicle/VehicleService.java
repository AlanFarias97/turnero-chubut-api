package com.turnerochubut.api.vehicle;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
class VehicleService {

    private static final List<Integer> BAY_IDS = List.of(1, 2, 3);

    private final VehicleRepository vehicleRepository;

    VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional(readOnly = true)
    DashboardStateResponse getDashboardState() {
        List<BayResponse> bays = BAY_IDS
            .stream()
            .map(this::buildBay)
            .toList();

        List<VehicleResponse> waiting = vehicleRepository
            .findByStatus(
                VehicleStatus.IN_QUEUE,
                Sort.by(Sort.Direction.ASC, "ticketNumber")
            )
            .stream()
            .map(VehicleResponse::from)
            .toList();

        List<VehicleResponse> completed = vehicleRepository
            .findByStatusIn(
                List.of(
                    VehicleStatus.COMPLETED,
                    VehicleStatus.PARTIAL_COMPLETED,
                    VehicleStatus.NOT_COMPLETED
                ),
                Sort.by(Sort.Direction.DESC, "boxEndedAt", "ticketNumber")
            )
            .stream()
            .map(VehicleResponse::from)
            .toList();

        return new DashboardStateResponse(
            bays,
            waiting,
            completed,
            vehicleRepository.findMaxTicketNumber() + 1
        );
    }

    @Transactional
    VehicleResponse createVehicle(VehicleRequest request) {
        Vehicle vehicle = new Vehicle(
            vehicleRepository.findMaxTicketNumber() + 1,
            normalizeRequired(request.patent()).toUpperCase(),
            normalizeRequired(request.description()),
            normalizeRequired(request.service()),
            request.paymentStatus()
        );

        vehicle.updateDetails(
            normalizeRequired(request.patent()).toUpperCase(),
            normalizeRequired(request.description()),
            normalizeRequired(request.service()),
            request.paymentStatus(),
            List.of()
        );

        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    @Transactional
    VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        Vehicle vehicle = findVehicle(id);
        vehicle.updateDetails(
            normalizeRequired(request.patent()).toUpperCase(),
            normalizeRequired(request.description()),
            normalizeRequired(request.service()),
            request.paymentStatus(),
            request.assignedOperators()
        );

        return VehicleResponse.from(vehicle);
    }

    @Transactional
    DashboardStateResponse assignToBay(Long id, AssignVehicleRequest request) {
        Vehicle vehicle = findVehicle(id);
        ensureCanReturnToWorkshop(vehicle);
        ensureBayExists(request.bayId());
        ensureBayIsAvailable(request.bayId(), vehicle.getId());

        boolean resetTimer =
            vehicle.getStatus() == VehicleStatus.PARTIAL_COMPLETED ||
            vehicle.getStatus() == VehicleStatus.NOT_COMPLETED ||
            vehicle.isResetBoxTimerOnNextAssignment();

        vehicle.assignToBay(
            request.bayId(),
            request.assignedOperators(),
            resetTimer
        );

        return getDashboardState();
    }

    @Transactional
    DashboardStateResponse moveToQueue(Long id) {
        Vehicle vehicle = findVehicle(id);
        ensureCanReturnToWorkshop(vehicle);
        vehicle.moveToQueue();
        return getDashboardState();
    }

    @Transactional
    DashboardStateResponse completeVehicle(Long id, CompleteVehicleRequest request) {
        Vehicle vehicle = findVehicle(id);
        if (vehicle.getStatus() != VehicleStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se puede finalizar un vehiculo en box");
        }

        VehicleStatus completionStatus = request.status();
        if (
            completionStatus != VehicleStatus.COMPLETED &&
            completionStatus != VehicleStatus.PARTIAL_COMPLETED &&
            completionStatus != VehicleStatus.NOT_COMPLETED
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de finalizacion invalido");
        }

        String pendingWorkDetail = normalizeOptional(request.pendingWorkDetail());
        if (completionStatus != VehicleStatus.COMPLETED && !StringUtils.hasText(pendingWorkDetail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe detallar el trabajo pendiente o motivo");
        }

        vehicle.complete(completionStatus, pendingWorkDetail);
        return getDashboardState();
    }

    private BayResponse buildBay(Integer bayId) {
        VehicleResponse vehicle = vehicleRepository
            .findByBayId(bayId)
            .map(VehicleResponse::from)
            .orElse(null);

        return new BayResponse(
            bayId,
            "BOX " + bayId,
            vehicle
        );
    }

    private Vehicle findVehicle(Long id) {
        return vehicleRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehiculo no encontrado"));
    }

    private void ensureBayExists(Integer bayId) {
        if (!BAY_IDS.contains(bayId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Box invalido");
        }
    }

    private void ensureBayIsAvailable(Integer bayId, Long vehicleId) {
        vehicleRepository
            .findByBayId(bayId)
            .filter(vehicle -> !vehicle.getId().equals(vehicleId))
            .ifPresent(_vehicle -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El box ya esta ocupado");
            });
    }

    private void ensureCanReturnToWorkshop(Vehicle vehicle) {
        if (vehicle.getStatus() == VehicleStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un trabajo completo no puede volver al taller");
        }
    }

    private String normalizeRequired(String value) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dato requerido");
        }

        return value.trim();
    }

    private String normalizeOptional(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }
}
