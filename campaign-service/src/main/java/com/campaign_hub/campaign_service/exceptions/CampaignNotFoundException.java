package com.campaign_hub.campaign_service.exceptions;

public class CampaignNotFoundException extends RuntimeException {
    public CampaignNotFoundException(Long id) {
        super("Kampanja sa ID " + id + " ne postoji");
    }
}