package com.turnerochubut.api.auth;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerLoginAndReadCurrentUser() throws Exception {
        String registerBody = objectMapper.writeValueAsString(Map.of(
            "email", "caja@turnerochubut.com",
            "firstName", "Caja",
            "lastName", "Chubut",
            "phoneNumber", "2804000000",
            "address", "Av. Taller 123",
            "password", "Caja-1",
            "role", "CAJERO"
        ));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token", notNullValue()))
            .andExpect(jsonPath("$.user.email").value("caja@turnerochubut.com"))
            .andExpect(jsonPath("$.user.displayName").value("Caja Chubut"))
            .andExpect(jsonPath("$.user.firstName").value("Caja"))
            .andExpect(jsonPath("$.user.lastName").value("Chubut"))
            .andExpect(jsonPath("$.user.phoneNumber").value("2804000000"))
            .andExpect(jsonPath("$.user.address").value("Av. Taller 123"))
            .andExpect(jsonPath("$.user.role").value("CAJERO"));

        String loginBody = objectMapper.writeValueAsString(Map.of(
            "email", "caja@turnerochubut.com",
            "password", "Caja-1"
        ));

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", notNullValue()))
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginResponse);
        String token = loginJson.get("token").asText();

        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("caja@turnerochubut.com"))
            .andExpect(jsonPath("$.authProvider").value("EMAIL"));
    }

    @Test
    void publicRegisterCannotCreateAdministrators() throws Exception {
        String requestBody = objectMapper.writeValueAsString(Map.of(
            "email", "admin@turnerochubut.com",
            "firstName", "Admin",
            "lastName", "Chubut",
            "phoneNumber", "2804000000",
            "address", "Av. Taller 123",
            "password", "Admin-1",
            "role", "ADMINISTRADOR"
        ));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isForbidden());
    }
}
