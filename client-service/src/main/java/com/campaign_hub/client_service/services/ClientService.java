package com.campaign_hub.client_service.services;

import com.campaign_hub.client_service.exceptions.ClientNotFoundException;
import com.campaign_hub.client_service.exceptions.DuplicateTaxIdException;
import com.campaign_hub.client_service.models.Client;
import com.campaign_hub.client_service.models.ClientStatus;
import com.campaign_hub.client_service.repositories.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAll() {
        return clientRepository.findAll();
    }

    public List<Client> getByStatus(ClientStatus status) {
        return clientRepository.findByStatus(status);
    }

    public Client getById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    public Client create(Client client) {
        if (clientRepository.existsByTaxId(client.getTaxId())) {
            log.warn("Pokušaj dupliranja PIB-a {}", client.getTaxId());
            throw new DuplicateTaxIdException(client.getTaxId());
        }
        Client saved = clientRepository.save(client);
        log.info("Kreiran klijent id={} name={}", saved.getClientId(), saved.getName());
        return saved;
    }

    public Client update(Long id, Client updated) {
        Client existing = getById(id);
        if (!existing.getTaxId().equals(updated.getTaxId())
                && clientRepository.existsByTaxId(updated.getTaxId())) {
            throw new DuplicateTaxIdException(updated.getTaxId());
        }
        existing.setName(updated.getName());
        existing.setTaxId(updated.getTaxId());
        existing.setEmail(updated.getEmail());
        existing.setIndustry(updated.getIndustry());
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        log.info("Izmenjen klijent id={}", id);
        return clientRepository.save(existing);
    }

    public void delete(Long id) {
        Client existing = getById(id);
        clientRepository.delete(existing);
        log.info("Obrisan klijent id={}", id);
    }
}