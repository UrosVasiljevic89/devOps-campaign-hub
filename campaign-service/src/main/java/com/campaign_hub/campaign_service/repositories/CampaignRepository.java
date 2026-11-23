package com.campaign_hub.campaign_service.repositories;

import com.campaign_hub.campaign_service.models.Campaign;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CampaignRepository {

    private final Map<Long, Campaign> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public Mono<Campaign> save(Campaign campaign) {
        return Mono.fromSupplier(() -> {
            if (campaign.getCampaignId() == null) {
                campaign.setCampaignId(sequence.incrementAndGet());
            }
            store.put(campaign.getCampaignId(), campaign);
            return campaign;
        });
    }

    public Mono<Campaign> findById(Long id) {
        return Mono.fromSupplier(() -> store.get(id));
    }

    public Flux<Campaign> findAll() {
        return Flux.defer(() -> Flux.fromIterable(new ArrayList<>(store.values())));
    }

    public Flux<Campaign> findByClientId(Long clientId) {
        return findAll().filter(c -> clientId.equals(c.getClientId()));
    }

    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> store.remove(id));
    }

    public void deleteAll() {
        store.clear();
    }
}