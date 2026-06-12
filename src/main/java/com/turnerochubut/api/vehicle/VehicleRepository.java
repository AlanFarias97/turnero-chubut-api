package com.turnerochubut.api.vehicle;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByStatus(VehicleStatus status, Sort sort);

    List<Vehicle> findByStatusIn(List<VehicleStatus> statuses, Sort sort);

    Optional<Vehicle> findByBayId(Integer bayId);

    boolean existsByBayId(Integer bayId);

    @Query("select coalesce(max(v.ticketNumber), 0) from Vehicle v")
    int findMaxTicketNumber();
}
