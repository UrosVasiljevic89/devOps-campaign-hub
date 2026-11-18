package com.campaign_hub.client_service.repositories;

import com.campaign_hub.client_service.models.Client;
import com.campaign_hub.client_service.models.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    boolean existsByTaxId(String taxId);

    List<Client> findByStatus(ClientStatus status);

    List<Client> findByNameContainingIgnoreCase(String name);
}
