package com.campaign_hub.campaign_service.controllers;

import com.campaign_hub.campaign_service.models.Campaign;
import com.campaign_hub.campaign_service.services.CampaignService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    public Flux<Campaign> getAll(@RequestParam(required = false) Long clientId) {
        if (clientId != null) {
            return campaignService.getByClient(clientId);
        }
        return campaignService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<Campaign> getById(@PathVariable Long id) {
        return campaignService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Campaign> create(@Valid @RequestBody Campaign campaign) {
        return campaignService.create(campaign);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return campaignService.delete(id);
    }
}