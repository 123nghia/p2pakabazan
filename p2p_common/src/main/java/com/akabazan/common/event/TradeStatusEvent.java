package com.akabazan.common.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class TradeStatusEvent implements Serializable {

    private UUID tradeId;
    private UUID orderId;
    private String status;
    private Double amount;
    private UUID buyerId;
    private UUID sellerId;
    private Instant occurredAt;

    public TradeStatusEvent() {
    }

    public TradeStatusEvent(UUID tradeId,
                            UUID orderId,
                            String status,
                            Double amount,
                            UUID buyerId,
                            UUID sellerId,
                            Instant occurredAt) {
        this.tradeId = tradeId;
        this.orderId = orderId;
        this.status = status;
        this.amount = amount;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.occurredAt = occurredAt;
    }

    public UUID getTradeId() {
        return tradeId;
    }

    public void setTradeId(UUID tradeId) {
        this.tradeId = tradeId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public UUID getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(UUID buyerId) {
        this.buyerId = buyerId;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
