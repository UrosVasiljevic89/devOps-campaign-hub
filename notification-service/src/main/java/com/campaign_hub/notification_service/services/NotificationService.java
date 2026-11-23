package com.campaign_hub.notification_service.services;

import com.campaign_hub.notification_service.exceptions.NotificationNotFoundException;
import com.campaign_hub.notification_service.models.Notification;
import com.campaign_hub.notification_service.repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification send(Notification notification) {
        notification.setStatus("SENT");
        notification.setSentAt(LocalDateTime.now());
        Notification saved = notificationRepository.save(notification);
        log.info("Obaveštenje poslato id={} type={} to={}",
                saved.getNotificationId(), saved.getType(), saved.getRecipient());
        return saved;
    }

    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    public Notification getById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
    }
}