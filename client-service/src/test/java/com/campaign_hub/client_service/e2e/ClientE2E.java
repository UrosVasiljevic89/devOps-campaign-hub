package com.campaign_hub.client_service.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ClientE2ETest {

    private static final String NEW_CLIENT_JSON = """
            {"name":"E2E Klijent","taxId":"444444444","email":"e2e@example.com","industry":"TECHNOLOGY"}
            """;

    @Autowired
    private TestRestTemplate rest;

    private HttpEntity<String> jsonBody(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }

    @Test
    void clientLifecycle_createReadDelete_overRealHttp() {
        ResponseEntity<Map> created = rest.postForEntity("/api/clients", jsonBody(NEW_CLIENT_JSON), Map.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode(), "Odgovor servera: " + created.getBody());
        Object id = created.getBody().get("clientId");
        assertNotNull(id);

        ResponseEntity<Map> fetched = rest.getForEntity("/api/clients/" + id, Map.class);
        assertEquals(HttpStatus.OK, fetched.getStatusCode());
        assertEquals("E2E Klijent", fetched.getBody().get("name"));
        assertEquals("ACTIVE", fetched.getBody().get("status"));

        rest.delete("/api/clients/" + id);

        ResponseEntity<Map> afterDelete = rest.getForEntity("/api/clients/" + id, Map.class);
        assertEquals(HttpStatus.NOT_FOUND, afterDelete.getStatusCode());
    }

    @Test
    void healthEndpoint_isUp() {
        ResponseEntity<String> health = rest.getForEntity("/actuator/health", String.class);

        assertEquals(HttpStatus.OK, health.getStatusCode());
        assertTrue(health.getBody().contains("UP"));
    }
}