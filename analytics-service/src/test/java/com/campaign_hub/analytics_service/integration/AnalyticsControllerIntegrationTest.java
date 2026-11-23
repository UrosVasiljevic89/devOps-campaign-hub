package com.campaign_hub.analytics_service.integration;

import com.campaign_hub.analytics_service.repositories.MetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnalyticsControllerIntegrationTest {

    private static final String DAY_1 = """
            {"date":"2026-11-20","impressions":10000,"clicks":250,"conversions":12,"spend":50,"revenue":180}
            """;
    private static final String DAY_2 = """
            {"date":"2026-11-21","impressions":10000,"clicks":150,"conversions":8,"spend":50,"revenue":120}
            """;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MetricRepository metricRepository;

    @BeforeEach
    void cleanStore() {
        metricRepository.deleteAll();
    }

    private void post(String json) {
        webTestClient.post().uri("/api/analytics/campaigns/1/metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void recordMetric_returns201() {
        post(DAY_1);
    }

    @Test
    void getMetrics_returnsAllDaysAsFlux() {
        post(DAY_1);
        post(DAY_2);

        webTestClient.get().uri("/api/analytics/campaigns/1/metrics")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].date").isEqualTo("2026-11-20");
    }

    @Test
    void getSummary_returnsTotalsAndRates() {
        post(DAY_1);
        post(DAY_2);

        webTestClient.get().uri("/api/analytics/campaigns/1/summary")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.days").isEqualTo(2)
                .jsonPath("$.clicks").isEqualTo(400)
                .jsonPath("$.roas").isEqualTo(3.0);
    }

    @Test
    void recordMetric_returns400_whenMoreClicksThanImpressions() {
        webTestClient.post().uri("/api/analytics/campaigns/1/metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"date":"2026-11-20","impressions":100,"clicks":500,"conversions":0,"spend":10,"revenue":0}
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").exists();
    }

    @Test
    void recordMetric_returns400_whenFieldsMissing() {
        webTestClient.post().uri("/api/analytics/campaigns/1/metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"impressions":-5}
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.date").exists()
                .jsonPath("$.impressions").exists();
    }
}