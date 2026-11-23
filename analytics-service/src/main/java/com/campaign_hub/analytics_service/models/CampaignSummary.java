package com.campaign_hub.analytics_service.models;

import java.math.BigDecimal;

public record CampaignSummary(
        Long campaignId,
        long days,
        long impressions,
        long clicks,
        long conversions,
        BigDecimal spend,
        BigDecimal revenue,
        Double ctr,
        Double roas
) {}