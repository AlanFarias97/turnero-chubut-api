package com.turnerochubut.api.health;

import java.time.OffsetDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/status")
class ApiStatusController {

    @GetMapping
    ResponseEntity<ApiStatusResponse> getStatus() {
        return ResponseEntity.ok(
            new ApiStatusResponse(
                "turnero-chubut-api",
                "ok",
                OffsetDateTime.now()
            )
        );
    }

    record ApiStatusResponse(
        String service,
        String status,
        OffsetDateTime timestamp
    ) {
    }
}
