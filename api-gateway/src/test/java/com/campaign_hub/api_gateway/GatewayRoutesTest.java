package com.campaign_hub.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GatewayRoutesTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void allFourServiceRoutesAreConfigured() {
        StepVerifier.create(routeLocator.getRoutes().map(Route::getId).collectList())
                .assertNext(ids -> assertTrue(
                        ids.containsAll(List.of("clients", "campaigns", "analytics", "notifications")),
                        "Rute: " + ids))
                .verifyComplete();
    }
}