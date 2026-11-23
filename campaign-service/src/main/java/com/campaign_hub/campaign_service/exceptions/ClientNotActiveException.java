package com.campaign_hub.campaign_service.exceptions;

public class ClientNotActiveException extends RuntimeException {
    public ClientNotActiveException(Long clientId, String status) {
        super("Klijent " + clientId + " nije aktivan (status: " + status + ")");
    }
}