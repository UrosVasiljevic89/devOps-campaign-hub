package com.campaign_hub.client_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    @SequenceGenerator(name = "client_seq", sequenceName = "client_seq", allocationSize = 1)
    private Long clientId;

    @NotBlank //ne sme biti null ili prazan string
    private String name;

    @NotBlank
    @Pattern(regexp = "\\d{9}", message = "PIB mora imati tačno 9 cifara")
    @Column(unique = true)
    private String taxId;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private Industry industry;

    @Enumerated(EnumType.STRING)
    private ClientStatus status;

    private LocalDateTime createdAt;

    public Client() {}

    public Client(String name, String taxId, String email, Industry industry) {
        this.name = name;
        this.taxId = taxId;
        this.email = email;
        this.industry = industry;
    }

    @PrePersist //
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = ClientStatus.ACTIVE;
    }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Industry getIndustry() { return industry; }
    public void setIndustry(Industry industry) { this.industry = industry; }

    public ClientStatus getStatus() { return status; }
    public void setStatus(ClientStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}