package com.campaign_hub.client_service.integration;

import com.campaign_hub.client_service.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClientControllerIntegrationTest {

    private static final String VALID_CLIENT = """
            {"name":"Kafic Centar","taxId":"123456789","email":"kafic@example.com","industry":"HOSPITALITY"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void cleanDatabase() {
        clientRepository.deleteAll();
    }

    @Test
    void createClient_returns201_withIdAndActiveStatus() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CLIENT))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientId").exists())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createClient_returns400_whenInputInvalid() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","taxId":"123","email":"nije-mejl"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.taxId").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void createClient_returns409_whenTaxIdDuplicate() throws Exception {
        mockMvc.perform(post("/api/clients").contentType(MediaType.APPLICATION_JSON).content(VALID_CLIENT))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/clients").contentType(MediaType.APPLICATION_JSON).content(VALID_CLIENT))
                .andExpect(status().isConflict());
    }

    @Test
    void getClient_returns404_whenMissing() throws Exception {
        mockMvc.perform(get("/api/clients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getClients_returns400_whenStatusUnknown() throws Exception {
        mockMvc.perform(get("/api/clients").param("status", "XYZ"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteClient_returns204_thenGetReturns404() throws Exception {
        String body = mockMvc.perform(post("/api/clients").contentType(MediaType.APPLICATION_JSON).content(VALID_CLIENT))
                .andReturn().getResponse().getContentAsString();
        String id = body.replaceAll(".*\"clientId\":(\\d+).*", "$1");

        mockMvc.perform(delete("/api/clients/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/clients/" + id))
                .andExpect(status().isNotFound());
    }
}