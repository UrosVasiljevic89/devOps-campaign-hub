package com.campaign_hub.notification_service.exceptions;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(Long id) {
        super("Obaveštenje sa ID " + id + " ne postoji");
    }
}