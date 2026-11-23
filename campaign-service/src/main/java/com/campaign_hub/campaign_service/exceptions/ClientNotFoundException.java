package com.campaign_hub.campaign_service.exceptions;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long clientId) {
        super("Klijent sa ID " + clientId + " ne postoji");
    }
}