package com.campaign_hub.notification_service.repositories;

import com.campaign_hub.notification_service.models.Notification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class NotificationRepository {

    private final Map<Long, Notification> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public Notification save(Notification notification) {
        if (notification.getNotificationId() == null) {
            notification.setNotificationId(sequence.incrementAndGet());
        }
        store.put(notification.getNotificationId(), notification);
        return notification;
    }

    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Notification> findAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteAll() {
        store.clear();
    }
}