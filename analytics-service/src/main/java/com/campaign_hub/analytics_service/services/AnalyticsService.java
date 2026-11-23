package com.campaign_hub.analytics_service.services;

import com.campaign_hub.analytics_service.exceptions.InvalidMetricException;
import com.campaign_hub.analytics_service.models.CampaignSummary;
import com.campaign_hub.analytics_service.models.DailyMetric;
import com.campaign_hub.analytics_service.repositories.MetricRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);

    private final MetricRepository metricRepository;

    public AnalyticsService(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    public Mono<DailyMetric> record(Long campaignId, DailyMetric metric) {
        if (metric.getClicks() > metric.getImpressions()
                || metric.getConversions() > metric.getClicks()) {
            return Mono.error(new InvalidMetricException(
                    "Mora važiti: prikazi >= klikovi >= konverzije"));
        }
        metric.setCampaignId(campaignId);
        return metricRepository.save(metric)
                .doOnNext(saved -> log.info("Zabeležena metrika kampanja={} datum={}",
                        campaignId, saved.getDate()));
    }

    public Flux<DailyMetric> getMetrics(Long campaignId) {
        return metricRepository.findByCampaignId(campaignId);
    }

    public Mono<CampaignSummary> getSummary(Long campaignId) {
        return metricRepository.findByCampaignId(campaignId)
                .collectList()
                .map(metrics -> summarize(campaignId, metrics));
    }

    private CampaignSummary summarize(Long campaignId, List<DailyMetric> metrics) {
        long impressions = metrics.stream().mapToLong(DailyMetric::getImpressions).sum();
        long clicks = metrics.stream().mapToLong(DailyMetric::getClicks).sum();
        long conversions = metrics.stream().mapToLong(DailyMetric::getConversions).sum();
        BigDecimal spend = metrics.stream().map(DailyMetric::getSpend).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal revenue = metrics.stream().map(DailyMetric::getRevenue).reduce(BigDecimal.ZERO, BigDecimal::add);

        Double ctr = impressions == 0 ? null : (double) clicks / impressions;
        Double roas = spend.signum() == 0 ? null
                : revenue.divide(spend, 4, RoundingMode.HALF_UP).doubleValue();

        return new CampaignSummary(campaignId, metrics.size(), impressions, clicks,
                conversions, spend, revenue, ctr, roas);
    }
}