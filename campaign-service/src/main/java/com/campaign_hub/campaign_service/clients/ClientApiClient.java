package com.campaign_hub.campaign_service.clients;

import com.campaign_hub.campaign_service.exceptions.ClientNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ClientApiClient {

    private final WebClient webClient;

    public ClientApiClient(WebClient.Builder builder,
                           @Value("${clients.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<ClientDto> findClient(Long clientId) {
        return webClient.get()
                .uri("/api/clients/{id}", clientId)
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> Mono.error(new ClientNotFoundException(clientId)))
                .bodyToMono(ClientDto.class);
    }
}