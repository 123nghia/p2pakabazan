package com.akabazan.common.event;

import java.io.Serializable;
import java.time.Instant;

public class TradeStatusEvent implements Serializable {

    private Long tradeId;
    private Long orderId;
    private String status;
    private Double amount;
    private Long buyerId;
    private Long sellerId;
    private Instant occurredAt;

    public TradeStatusEvent() {
    }

    public TradeStatusEvent(Long tradeId,
                            Long orderId,
                            String status,
                            Double amount,
                            Long buyerId,
                            Long sellerId,
                            Instant occurredAt) {
        this.tradeId = tradeId;
        this.orderId = orderId;
        this.status = status;
        this.amount = amount;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.occurredAt = occurredAt;
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
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

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}

