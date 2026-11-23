package com.campaign_hub.analytics_service.controllers;

import com.campaign_hub.analytics_service.models.CampaignSummary;
import com.campaign_hub.analytics_service.models.DailyMetric;
import com.campaign_hub.analytics_service.services.AnalyticsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/analytics/campaigns/{campaignId}")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/metrics")
    public Flux<DailyMetric> getMetrics(@PathVariable Long campaignId) {
        return analyticsService.getMetrics(campaignId);
    }

    @PostMapping("/metrics")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DailyMetric> record(@PathVariable Long campaignId,
                                    @Valid @RequestBody DailyMetric metric) {
        return analyticsService.record(campaignId, metric);
    }

    @GetMapping("/summary")
    public Mono<CampaignSummary> getSummary(@PathVariable Long campaignId) {
        return analyticsService.getSummary(campaignId);
    }
}