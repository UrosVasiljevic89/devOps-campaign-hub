package com.campaign_hub.campaign_service.service;

import com.campaign_hub.campaign_service.clients.ClientApiClient;
import com.campaign_hub.campaign_service.clients.ClientDto;
import com.campaign_hub.campaign_service.exceptions.CampaignNotFoundException;
import com.campaign_hub.campaign_service.exceptions.ClientNotActiveException;
import com.campaign_hub.campaign_service.exceptions.ClientNotFoundException;
import com.campaign_hub.campaign_service.models.Campaign;
import com.campaign_hub.campaign_service.models.CampaignStatus;
import com.campaign_hub.campaign_service.models.Channel;
import com.campaign_hub.campaign_service.repositories.CampaignRepository;
import com.campaign_hub.campaign_service.services.CampaignService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private ClientApiClient clientApiClient;

    @InjectMocks
    private CampaignService campaignService;

    private Campaign newCampaign() {
        return new Campaign(1L, "Black Friday", Channel.META_ADS, new BigDecimal("500"));
    }

    @Test
    void create_savesAsDraft_whenClientIsActive() {
        when(clientApiClient.findClient(1L)).thenReturn(Mono.just(new ClientDto(1L, "Kafic", "ACTIVE")));
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(campaignService.create(newCampaign()))
                .assertNext(saved -> {
                    assertEquals(CampaignStatus.DRAFT, saved.getStatus());
                    assertNotNull(saved.getCreatedAt());
                })
                .verifyComplete();
    }

    @Test
    void create_failsAndDoesNotSave_whenClientNotActive() {
        when(clientApiClient.findClient(1L)).thenReturn(Mono.just(new ClientDto(1L, "Kafic", "PAUSED")));

        StepVerifier.create(campaignService.create(newCampaign()))
                .expectError(ClientNotActiveException.class)
                .verify();

        verify(campaignRepository, never()).save(any());
    }

    @Test
    void create_failsAndDoesNotSave_whenClientMissing() {
        when(clientApiClient.findClient(1L)).thenReturn(Mono.error(new ClientNotFoundException(1L)));

        StepVerifier.create(campaignService.create(newCampaign()))
                .expectError(ClientNotFoundException.class)
                .verify();

        verify(campaignRepository, never()).save(any());
    }

    @Test
    void getById_fails_whenCampaignMissing() {
        when(campaignRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(campaignService.getById(99L))
                .expectError(CampaignNotFoundException.class)
                .verify();
    }

    @Test
    void getAll_returnsAllCampaigns() {
        when(campaignRepository.findAll()).thenReturn(Flux.just(newCampaign(), newCampaign()));

        StepVerifier.create(campaignService.getAll())
                .expectNextCount(2)
                .verifyComplete();
    }
}