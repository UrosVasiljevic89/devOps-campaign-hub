package com.campaign_hub.campaign_service.integration;

import com.campaign_hub.campaign_service.clients.ClientApiClient;
import com.campaign_hub.campaign_service.clients.ClientDto;
import com.campaign_hub.campaign_service.exceptions.ClientNotFoundException;
import com.campaign_hub.campaign_service.repositories.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CampaignControllerIntegrationTest {

    private static final String VALID_CAMPAIGN = """
            {"clientId":1,"name":"Black Friday","channel":"META_ADS","budget":500}
            """;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CampaignRepository campaignRepository;

    @MockitoBean
    private ClientApiClient clientApiClient;

    @BeforeEach
    void cleanStore() {
        campaignRepository.deleteAll();
    }

    @Test
    void createCampaign_returns201_asDraft_whenClientActive() {
        when(clientApiClient.findClient(1L)).thenReturn(Mono.just(new ClientDto(1L, "Kafic", "ACTIVE")));

        webTestClient.post().uri("/api/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(VALID_CAMPAIGN)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.campaignId").exists()
                .jsonPath("$.status").isEqualTo("DRAFT");
    }

    @Test
    void createCampaign_returns409_whenClientNotActive() {
        when(clientApiClient.findClient(1L)).thenReturn(Mono.just(new ClientDto(1L, "Kafic", "CHURNED")));

        webTestClient.post().uri("/api/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(VALID_CAMPAIGN)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void createCampaign_returns400_whenClientMissing() {
        when(clientApiClient.findClient(1L)).thenReturn(Mono.error(new ClientNotFoundException(1L)));

        webTestClient.post().uri("/api/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(VALID_CAMPAIGN)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").exists();
    }

    @Test
    void createCampaign_returns400_whenInputInvalid() {
        webTestClient.post().uri("/api/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name":"","channel":"SEO","budget":-5}
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.clientId").exists()
                .jsonPath("$.name").exists()
                .jsonPath("$.budget").exists();
    }

    @Test
    void getCampaign_returns404_whenMissing() {
        webTestClient.get().uri("/api/campaigns/999")
                .exchange()
                .expectStatus().isNotFound();
    }
    
    @Test
    void unknownUrl_returns404_notServerError() {
        webTestClient.get().uri("/api/nepostojeci")
                .exchange()
                .expectStatus().isNotFound();
    }
}