package com.campaign_hub.client_service.repository;

import com.campaign_hub.client_service.models.Client;
import com.campaign_hub.client_service.models.ClientStatus;
import com.campaign_hub.client_service.models.Industry;
import com.campaign_hub.client_service.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ClientRepositoryTest {

    @Autowired
    private ClientRepository repo;

    @Test
    void save_setsIdCreatedAtAndDefaultStatus() {
        Client saved = repo.save(new Client("Kafic", "123456789", "k@example.com", Industry.HOSPITALITY));

        assertNotNull(saved.getClientId());
        assertNotNull(saved.getCreatedAt());
        assertEquals(ClientStatus.ACTIVE, saved.getStatus());
    }

    @Test
    void existsByTaxId_returnsTrueOnlyForSavedTaxId() {
        repo.save(new Client("Kafic", "123456789", "k@example.com", Industry.HOSPITALITY));

        assertTrue(repo.existsByTaxId("123456789"));
        assertFalse(repo.existsByTaxId("000000000"));
    }

    @Test
    void findByStatus_returnsOnlyMatchingClients() {
        repo.save(new Client("Aktivan", "111111111", "a@example.com", Industry.OTHER));
        Client paused = new Client("Pauziran", "222222222", "p@example.com", Industry.FINANCE);
        paused.setStatus(ClientStatus.PAUSED);
        repo.save(paused);

        List<Client> result = repo.findByStatus(ClientStatus.PAUSED);

        assertEquals(1, result.size());
        assertEquals("Pauziran", result.get(0).getName());
    }

    @Test
    void findByNameContainingIgnoreCase_findsByPartOfName() {
        repo.save(new Client("Kafic Centar", "123456789", "k@example.com", Industry.HOSPITALITY));

        assertEquals(1, repo.findByNameContainingIgnoreCase("centar").size());
    }
}