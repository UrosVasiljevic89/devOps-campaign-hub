package com.campaign_hub.client_service.exceptions;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long id) {
        super("Klijent sa ID " + id + " ne postoji");
    }
}