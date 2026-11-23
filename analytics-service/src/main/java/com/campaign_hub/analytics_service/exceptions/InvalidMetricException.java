package com.campaign_hub.analytics_service.exceptions;

public class InvalidMetricException extends RuntimeException {
    public InvalidMetricException(String message) {
        super(message);
    }
}