package com.campaign_hub.campaign_service.models;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Campaign {

    private Long campaignId;

    @NotNull
    private Long clientId;

    @NotBlank
    private String name;

    @NotNull
    private Channel channel;

    @NotNull
    @Positive
    private BigDecimal budget;

    private CampaignStatus status;

    private LocalDateTime createdAt;

    public Campaign() {}

    public Campaign(Long clientId, String name, Channel channel, BigDecimal budget) {
        this.clientId = clientId;
        this.name = name;
        this.channel = channel;
        this.budget = budget;
    }

    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Channel getChannel() { return channel; }
    public void setChannel(Channel channel) { this.channel = channel; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public CampaignStatus getStatus() { return status; }
    public void setStatus(CampaignStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}