package com.campaign_hub.analytics_service.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyMetric {

    private Long campaignId;

    @NotNull
    private LocalDate date;

    @NotNull @PositiveOrZero
    private Long impressions;

    @NotNull @PositiveOrZero
    private Long clicks;

    @NotNull @PositiveOrZero
    private Long conversions;

    @NotNull @PositiveOrZero
    private BigDecimal spend;

    @NotNull @PositiveOrZero
    private BigDecimal revenue;

    public DailyMetric() {}

    public DailyMetric(LocalDate date, Long impressions, Long clicks, Long conversions,
                       BigDecimal spend, BigDecimal revenue) {
        this.date = date;
        this.impressions = impressions;
        this.clicks = clicks;
        this.conversions = conversions;
        this.spend = spend;
        this.revenue = revenue;
    }

    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getImpressions() { return impressions; }
    public void setImpressions(Long impressions) { this.impressions = impressions; }

    public Long getClicks() { return clicks; }
    public void setClicks(Long clicks) { this.clicks = clicks; }

    public Long getConversions() { return conversions; }
    public void setConversions(Long conversions) { this.conversions = conversions; }

    public BigDecimal getSpend() { return spend; }
    public void setSpend(BigDecimal spend) { this.spend = spend; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}