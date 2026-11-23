package com.campaign_hub.analytics_service.service;

import com.campaign_hub.analytics_service.exceptions.InvalidMetricException;
import com.campaign_hub.analytics_service.models.DailyMetric;
import com.campaign_hub.analytics_service.repositories.MetricRepository;
import com.campaign_hub.analytics_service.services.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private MetricRepository metricRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private DailyMetric metric(String date, long impressions, long clicks, long conversions,
                               String spend, String revenue) {
        return new DailyMetric(LocalDate.parse(date), impressions, clicks, conversions,
                new BigDecimal(spend), new BigDecimal(revenue));
    }

    @Test
    void record_savesMetricWithCampaignId_whenValid() {
        when(metricRepository.save(any(DailyMetric.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(analyticsService.record(1L, metric("2026-11-20", 10000, 250, 12, "50", "180")))
                .assertNext(saved -> assertEquals(1L, saved.getCampaignId()))
                .verifyComplete();
    }

    @Test
    void record_failsAndDoesNotSave_whenMoreClicksThanImpressions() {
        StepVerifier.create(analyticsService.record(1L, metric("2026-11-20", 100, 500, 0, "10", "0")))
                .expectError(InvalidMetricException.class)
                .verify();

        verify(metricRepository, never()).save(any());
    }

    @Test
    void record_failsAndDoesNotSave_whenMoreConversionsThanClicks() {
        StepVerifier.create(analyticsService.record(1L, metric("2026-11-20", 1000, 10, 50, "10", "0")))
                .expectError(InvalidMetricException.class)
                .verify();

        verify(metricRepository, never()).save(any());
    }

    @Test
    void getSummary_sumsDaysAndCalculatesCtrAndRoas() {
        when(metricRepository.findByCampaignId(1L)).thenReturn(Flux.just(
                metric("2026-11-20", 10000, 250, 12, "50", "180"),
                metric("2026-11-21", 10000, 150, 8, "50", "120")));

        StepVerifier.create(analyticsService.getSummary(1L))
                .assertNext(summary -> {
                    assertEquals(2, summary.days());
                    assertEquals(20000, summary.impressions());
                    assertEquals(400, summary.clicks());
                    assertEquals(0.02, summary.ctr(), 0.0001);
                    assertEquals(3.0, summary.roas(), 0.0001);
                })
                .verifyComplete();
    }

    @Test
    void getSummary_returnsNullRates_whenNoData() {
        when(metricRepository.findByCampaignId(99L)).thenReturn(Flux.empty());

        StepVerifier.create(analyticsService.getSummary(99L))
                .assertNext(summary -> {
                    assertEquals(0, summary.days());
                    assertNull(summary.ctr());
                    assertNull(summary.roas());
                })
                .verifyComplete();
    }
}