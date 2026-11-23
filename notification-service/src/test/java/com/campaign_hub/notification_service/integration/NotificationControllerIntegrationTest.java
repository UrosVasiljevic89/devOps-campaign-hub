package com.campaign_hub.notification_service.integration;

import com.campaign_hub.notification_service.repositories.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

    private static final String VALID = """
            {"type":"CAMPAIGN_CREATED","recipient":"tim@ulterius.com","message":"Nova kampanja"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void cleanStore() {
        notificationRepository.deleteAll();
    }

    @Test
    void sendNotification_returns201_withStatusSent() throws Exception {
        mockMvc.perform(post("/api/notifications").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationId").exists())
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void sendNotification_returns400_whenInputInvalid() throws Exception {
        mockMvc.perform(post("/api/notifications").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"recipient":"nije-mejl","message":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.recipient").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getAll_returnsSentNotifications() throws Exception {
        mockMvc.perform(post("/api/notifications").contentType(MediaType.APPLICATION_JSON).content(VALID));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getById_returns404_whenMissing() throws Exception {
        mockMvc.perform(get("/api/notifications/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unknownUrl_returns404_notServerError() throws Exception {
        mockMvc.perform(get("/api/nepostojeci"))
                .andExpect(status().isNotFound());
    }
}