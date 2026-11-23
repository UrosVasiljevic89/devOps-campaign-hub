package com.campaign_hub.notification_service.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class Notification {

    private Long notificationId;

    @NotNull
    private NotificationType type;

    @NotBlank
    @Email
    private String recipient;

    @NotBlank
    private String message;

    private String status;

    private LocalDateTime sentAt;

    public Notification() {}

    public Notification(NotificationType type, String recipient, String message) {
        this.type = type;
        this.recipient = recipient;
        this.message = message;
    }

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}