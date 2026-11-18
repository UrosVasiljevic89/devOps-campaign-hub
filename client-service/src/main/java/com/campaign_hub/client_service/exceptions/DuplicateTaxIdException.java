package com.campaign_hub.client_service.exceptions;

public class DuplicateTaxIdException extends RuntimeException {
    public DuplicateTaxIdException(String taxId) {
        super("Klijent sa PIB-om " + taxId + " već postoji");
    }
}