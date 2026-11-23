package com.campaign_hub.notification_service.service;

import com.campaign_hub.notification_service.exceptions.NotificationNotFoundException;
import com.campaign_hub.notification_service.models.Notification;
import com.campaign_hub.notification_service.models.NotificationType;
import com.campaign_hub.notification_service.repositories.NotificationRepository;
import com.campaign_hub.notification_service.services.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void send_marksAsSent_andSaves() {
        Notification n = new Notification(NotificationType.CAMPAIGN_CREATED, "tim@ulterius.com", "Nova kampanja");
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        Notification result = notificationService.send(n);

        assertEquals("SENT", result.getStatus());
        assertNotNull(result.getSentAt());
        verify(notificationRepository).save(n);
    }

    @Test
    void getById_throwsNotFound_whenMissing() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> notificationService.getById(99L));
    }
}