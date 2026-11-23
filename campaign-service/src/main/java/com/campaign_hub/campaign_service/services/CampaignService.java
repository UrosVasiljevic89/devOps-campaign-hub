package com.campaign_hub.campaign_service.services;

import com.campaign_hub.campaign_service.clients.ClientApiClient;
import com.campaign_hub.campaign_service.exceptions.CampaignNotFoundException;
import com.campaign_hub.campaign_service.exceptions.ClientNotActiveException;
import com.campaign_hub.campaign_service.models.Campaign;
import com.campaign_hub.campaign_service.models.CampaignStatus;
import com.campaign_hub.campaign_service.repositories.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class CampaignService {

    private static final Logger log = LoggerFactory.getLogger(CampaignService.class);

    private final CampaignRepository campaignRepository;
    private final ClientApiClient clientApiClient;

    public CampaignService(CampaignRepository campaignRepository, ClientApiClient clientApiClient) {
        this.campaignRepository = campaignRepository;
        this.clientApiClient = clientApiClient;
    }

    public Flux<Campaign> getAll() {
        return campaignRepository.findAll();
    }

    public Flux<Campaign> getByClient(Long clientId) {
        return campaignRepository.findByClientId(clientId);
    }

    public Mono<Campaign> getById(Long id) {
        return campaignRepository.findById(id)
                .switchIfEmpty(Mono.error(new CampaignNotFoundException(id)));
    }

    public Mono<Campaign> create(Campaign campaign) {
        return clientApiClient.findClient(campaign.getClientId())
                .flatMap(client -> {
                    if (!"ACTIVE".equals(client.status())) {
                        log.warn("Odbijena kampanja: klijent {} ima status {}", client.clientId(), client.status());
                        return Mono.error(new ClientNotActiveException(client.clientId(), client.status()));
                    }
                    campaign.setStatus(CampaignStatus.DRAFT);
                    campaign.setCreatedAt(LocalDateTime.now());
                    return campaignRepository.save(campaign);
                })
                .doOnNext(saved -> log.info("Kreirana kampanja id={} clientId={}",
                        saved.getCampaignId(), saved.getClientId()));
    }

    public Mono<Void> delete(Long id) {
        return getById(id)
                .flatMap(existing -> campaignRepository.deleteById(id))
                .doOnSuccess(v -> log.info("Obrisana kampanja id={}", id));
    }
}