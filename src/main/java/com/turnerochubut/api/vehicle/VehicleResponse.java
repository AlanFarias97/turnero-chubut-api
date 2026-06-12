package com.turnerochubut.api.vehicle;

import java.time.OffsetDateTime;
import java.util.List;

record VehicleResponse(
    Long id,
    int ticketNumber,
    String patent,
    String service,
    int waitingMinutes,
    VehicleStatus status,
    VehiclePaymentStatus paymentStatus,
    OffsetDateTime createdAt,
    OffsetDateTime boxStartedAt,
    OffsetDateTime boxTimerStartedAt,
    OffsetDateTime boxEndedAt,
    long boxElapsedMs,
    boolean resetBoxTimerOnNextAssignment,
    String description,
    List<String> assignedOperators,
    String pendingWorkDetail
) {
    static VehicleResponse from(Vehicle vehicle) {
        return new VehicleResponse(
            vehicle.getId(),
            vehicle.getTicketNumber(),
            vehicle.getPatent(),
            vehicle.getService(),
            calculateWaitingMinutes(vehicle),
            vehicle.getStatus(),
            vehicle.getPaymentStatus(),
            vehicle.getCreatedAt(),
            vehicle.getBoxStartedAt(),
            vehicle.getBoxTimerStartedAt(),
            vehicle.getBoxEndedAt(),
            vehicle.getBoxElapsedMs(),
            vehicle.isResetBoxTimerOnNextAssignment(),
            vehicle.getDescription(),
            List.copyOf(vehicle.getAssignedOperators()),
            vehicle.getPendingWorkDetail()
        );
    }

    private static int calculateWaitingMinutes(Vehicle vehicle) {
        if (vehicle.getCreatedAt() == null) {
            return 0;
        }

        OffsetDateTime end = vehicle.getBoxStartedAt();
        if (end == null && vehicle.getStatus() == VehicleStatus.IN_QUEUE) {
            end = OffsetDateTime.now();
        }

        if (end == null) {
            end = vehicle.getBoxEndedAt();
        }

        if (end == null) {
            return 0;
        }

        long minutes = java.time.Duration
            .between(vehicle.getCreatedAt(), end)
            .toMinutes();

        return (int) Math.max(minutes, 0);
    }
}
