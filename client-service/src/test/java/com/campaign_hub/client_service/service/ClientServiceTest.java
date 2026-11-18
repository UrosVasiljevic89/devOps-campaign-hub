package com.campaign_hub.client_service.service;

import com.campaign_hub.client_service.exceptions.ClientNotFoundException;
import com.campaign_hub.client_service.models.Client;
import com.campaign_hub.client_service.models.ClientStatus;
import com.campaign_hub.client_service.models.Industry;
import com.campaign_hub.client_service.repositories.ClientRepository;
import com.campaign_hub.client_service.services.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.campaign_hub.client_service.exceptions.DuplicateTaxIdException;
import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client sampleClient() {
        Client c = new Client("Kafic Centar", "123456789", "kafic@example.com", Industry.HOSPITALITY);
        c.setClientId(1L);
        c.setStatus(ClientStatus.ACTIVE);
        return c;
    }

    @Test
    void getById_returnsClient_whenExists() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(sampleClient()));

        Client result = clientService.getById(1L);

        assertEquals("Kafic Centar", result.getName());
    }

        @Test
    void create_savesClient_whenTaxIdIsNew() {
        Client newClient = new Client("Novi", "987654321", "novi@example.com", Industry.OTHER);
        when(clientRepository.existsByTaxId("987654321")).thenReturn(false);
        when(clientRepository.save(newClient)).thenReturn(newClient);

        Client result = clientService.create(newClient);

        assertSame(newClient, result);
        verify(clientRepository).save(newClient);
    }

    @Test
    void create_throwsDuplicate_andDoesNotSave_whenTaxIdExists() {
        Client duplicate = new Client("Duplikat", "123456789", "dup@example.com", Industry.OTHER);
        when(clientRepository.existsByTaxId("123456789")).thenReturn(true);

        assertThrows(DuplicateTaxIdException.class, () -> clientService.create(duplicate));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void update_changesFields_andKeepsCreatedAt() {
        Client existing = sampleClient();
        LocalDateTime created = LocalDateTime.of(2026, 1, 1, 10, 0);
        existing.setCreatedAt(created);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        Client changes = new Client("Kafic Centar NS", "123456789", "ns@example.com", Industry.HOSPITALITY);
        changes.setStatus(ClientStatus.PAUSED);

        Client result = clientService.update(1L, changes);

        assertEquals("Kafic Centar NS", result.getName());
        assertEquals(ClientStatus.PAUSED, result.getStatus());
        assertEquals(created, result.getCreatedAt());
    }

    @Test
    void update_throwsDuplicate_whenNewTaxIdBelongsToSomeoneElse() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(sampleClient()));
        when(clientRepository.existsByTaxId("111111111")).thenReturn(true);

        Client changes = new Client("Kafic", "111111111", "k@example.com", Industry.HOSPITALITY);

        assertThrows(DuplicateTaxIdException.class, () -> clientService.update(1L, changes));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.delete(99L));
        verify(clientRepository, never()).delete(any());
    }

    @Test
    void getById_throwsNotFound_whenMissing() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.getById(99L));
    }
}