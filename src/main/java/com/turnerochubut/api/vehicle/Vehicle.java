package com.turnerochubut.api.vehicle;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_number", nullable = false, unique = true)
    private int ticketNumber;

    @Column(nullable = false, length = 30)
    private String patent;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String service;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VehicleStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private VehiclePaymentStatus paymentStatus;

    @Column(name = "bay_id")
    private Integer bayId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "box_started_at")
    private OffsetDateTime boxStartedAt;

    @Column(name = "box_timer_started_at")
    private OffsetDateTime boxTimerStartedAt;

    @Column(name = "box_ended_at")
    private OffsetDateTime boxEndedAt;

    @Column(name = "box_elapsed_ms", nullable = false)
    private long boxElapsedMs;

    @Column(name = "reset_box_timer_on_next_assignment", nullable = false)
    private boolean resetBoxTimerOnNextAssignment;

    @Column(name = "pending_work_detail", columnDefinition = "TEXT")
    private String pendingWorkDetail;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "vehicle_assigned_operators",
        joinColumns = @JoinColumn(name = "vehicle_id")
    )
    @OrderColumn(name = "operator_order")
    @Column(name = "operator_name", nullable = false, length = 80)
    private List<String> assignedOperators = new ArrayList<>();

    protected Vehicle() {
    }

    Vehicle(
        int ticketNumber,
        String patent,
        String description,
        String service,
        VehiclePaymentStatus paymentStatus
    ) {
        this.ticketNumber = ticketNumber;
        this.patent = patent;
        this.description = description;
        this.service = service;
        this.paymentStatus = paymentStatus;
        this.status = VehicleStatus.IN_QUEUE;
        this.boxElapsedMs = 0;
    }

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    Long getId() {
        return id;
    }

    int getTicketNumber() {
        return ticketNumber;
    }

    String getPatent() {
        return patent;
    }

    String getDescription() {
        return description;
    }

    String getService() {
        return service;
    }

    VehicleStatus getStatus() {
        return status;
    }

    VehiclePaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    Integer getBayId() {
        return bayId;
    }

    OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    OffsetDateTime getBoxStartedAt() {
        return boxStartedAt;
    }

    OffsetDateTime getBoxTimerStartedAt() {
        return boxTimerStartedAt;
    }

    OffsetDateTime getBoxEndedAt() {
        return boxEndedAt;
    }

    long getBoxElapsedMs() {
        return boxElapsedMs;
    }

    boolean isResetBoxTimerOnNextAssignment() {
        return resetBoxTimerOnNextAssignment;
    }

    String getPendingWorkDetail() {
        return pendingWorkDetail;
    }

    List<String> getAssignedOperators() {
        return assignedOperators;
    }

    void updateDetails(
        String patent,
        String description,
        String service,
        VehiclePaymentStatus paymentStatus,
        List<String> assignedOperators
    ) {
        this.patent = patent;
        this.description = description;
        this.service = service;
        this.paymentStatus = paymentStatus;
        setAssignedOperators(assignedOperators);
    }

    void assignToBay(Integer bayId, List<String> assignedOperators, boolean resetTimer) {
        OffsetDateTime now = OffsetDateTime.now();
        status = VehicleStatus.IN_PROGRESS;
        this.bayId = bayId;
        boxStartedAt = resetTimer || boxStartedAt == null ? now : boxStartedAt;
        boxTimerStartedAt = now;
        boxEndedAt = null;
        boxElapsedMs = resetTimer ? 0 : boxElapsedMs;
        resetBoxTimerOnNextAssignment = false;
        pendingWorkDetail = null;
        setAssignedOperators(assignedOperators);
    }

    void moveToQueue() {
        pauseBoxTimer();
        status = VehicleStatus.IN_QUEUE;
        bayId = null;
        setAssignedOperators(List.of());
    }

    void complete(VehicleStatus completionStatus, String pendingWorkDetail) {
        pauseBoxTimer();
        status = completionStatus;
        bayId = null;
        resetBoxTimerOnNextAssignment = completionStatus != VehicleStatus.COMPLETED;
        this.pendingWorkDetail = completionStatus == VehicleStatus.COMPLETED ? null : pendingWorkDetail;
    }

    private void pauseBoxTimer() {
        boxElapsedMs = currentBoxElapsedMs();
        boxTimerStartedAt = null;
        boxEndedAt = OffsetDateTime.now();
    }

    private long currentBoxElapsedMs() {
        if (status != VehicleStatus.IN_PROGRESS || boxTimerStartedAt == null) {
            return boxElapsedMs;
        }

        long runningMs = java.time.Duration
            .between(boxTimerStartedAt, OffsetDateTime.now())
            .toMillis();

        return Math.max(boxElapsedMs + runningMs, 0);
    }

    private void setAssignedOperators(List<String> operators) {
        assignedOperators.clear();
        if (operators == null) {
            return;
        }

        operators
            .stream()
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .distinct()
            .forEach(assignedOperators::add);
    }
}
