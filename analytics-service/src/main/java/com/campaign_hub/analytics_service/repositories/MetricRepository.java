package com.campaign_hub.analytics_service.repositories;

import com.campaign_hub.analytics_service.models.DailyMetric;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MetricRepository {

    private final Map<String, DailyMetric> store = new ConcurrentHashMap<>();

    private String key(Long campaignId, Object date) {
        return campaignId + ":" + date;
    }

    public Mono<DailyMetric> save(DailyMetric metric) {
        return Mono.fromSupplier(() -> {
            store.put(key(metric.getCampaignId(), metric.getDate()), metric);
            return metric;
        });
    }

    public Flux<DailyMetric> findByCampaignId(Long campaignId) {
        return Flux.defer(() -> Flux.fromIterable(new ArrayList<>(store.values())))
                .filter(m -> campaignId.equals(m.getCampaignId()))
                .sort(Comparator.comparing(DailyMetric::getDate));
    }

    public void deleteAll() {
        store.clear();
    }
}