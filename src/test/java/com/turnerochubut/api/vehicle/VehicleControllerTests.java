package com.turnerochubut.api.vehicle;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VehicleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void managesVehicleDashboardFlow() throws Exception {
        String token = registerAndLogin("vehiculos@turnerochubut.com");

        String createBody = objectMapper.writeValueAsString(Map.of(
            "patent", "AB123CD",
            "description", "Gris",
            "service", "<ul><li>Alineacion auto</li></ul>",
            "paymentStatus", "partial"
        ));

        String createResponse = mockMvc.perform(post("/api/vehicles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.status").value("in_queue"))
            .andExpect(jsonPath("$.paymentStatus").value("partial"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        long vehicleId = objectMapper
            .readTree(createResponse)
            .get("id")
            .asLong();

        String assignBody = objectMapper.writeValueAsString(Map.of(
            "bayId", 1,
            "assignedOperators", List.of("Chino")
        ));

        mockMvc.perform(post("/api/vehicles/" + vehicleId + "/assign-to-bay")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bays[0].currentVehicle.status").value("in_progress"))
            .andExpect(jsonPath("$.bays[0].currentVehicle.assignedOperators[0]").value("Chino"));

        String partialBody = objectMapper.writeValueAsString(Map.of(
            "status", "partial_completed",
            "pendingWorkDetail", "Falta alineacion por rotula con juego"
        ));

        mockMvc.perform(post("/api/vehicles/" + vehicleId + "/complete")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(partialBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completedVehicles[0].status").value("partial_completed"))
            .andExpect(jsonPath("$.completedVehicles[0].resetBoxTimerOnNextAssignment").value(true));

        String returnToBayBody = objectMapper.writeValueAsString(Map.of(
            "bayId", 2,
            "assignedOperators", List.of("Lucas")
        ));

        mockMvc.perform(post("/api/vehicles/" + vehicleId + "/assign-to-bay")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(returnToBayBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bays[1].currentVehicle.status").value("in_progress"))
            .andExpect(jsonPath("$.bays[1].currentVehicle.boxElapsedMs").value(0))
            .andExpect(jsonPath("$.bays[1].currentVehicle.resetBoxTimerOnNextAssignment").value(false));
    }

    @Test
    void completedVehiclesCannotReturnToWorkshop() throws Exception {
        String token = registerAndLogin("completo@turnerochubut.com");
        long vehicleId = createVehicle(token, "CD456EF");

        String assignBody = objectMapper.writeValueAsString(Map.of(
            "bayId", 1,
            "assignedOperators", List.of("Chino")
        ));

        mockMvc.perform(post("/api/vehicles/" + vehicleId + "/assign-to-bay")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignBody))
            .andExpect(status().isOk());

        String completedBody = objectMapper.writeValueAsString(Map.of(
            "status", "completed",
            "pendingWorkDetail", ""
        ));

        mockMvc.perform(post("/api/vehicles/" + vehicleId + "/complete")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(completedBody))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/vehicles/" + vehicleId + "/assign-to-bay")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignBody))
            .andExpect(status().isConflict());
    }

    private long createVehicle(String token, String patent) throws Exception {
        String createBody = objectMapper.writeValueAsString(Map.of(
            "patent", patent,
            "description", "Gris",
            "service", "<ul><li>Balanceo auto</li></ul>",
            "paymentStatus", "unpaid"
        ));

        String response = mockMvc.perform(post("/api/vehicles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return objectMapper
            .readTree(response)
            .get("id")
            .asLong();
    }

    private String registerAndLogin(String email) throws Exception {
        String registerBody = objectMapper.writeValueAsString(Map.of(
            "email", email,
            "firstName", "Caja",
            "lastName", "Vehiculos",
            "phoneNumber", "2804000000",
            "address", "Av. Taller 123",
            "password", "Caja-1",
            "role", "CAJERO"
        ));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody))
            .andExpect(status().isCreated());

        String loginBody = objectMapper.writeValueAsString(Map.of(
            "email", email,
            "password", "Caja-1"
        ));

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("token").asText();
    }
}
