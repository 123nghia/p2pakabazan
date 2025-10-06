package com.akabazan.repository.entity;

import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.util.SnowflakeIdGenerator;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trades")
public class Trade extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "trade_code", nullable = false, unique = true, length = 32)
    private String tradeCode;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus status = TradeStatus.PENDING;

    @Column(nullable = false)
    private boolean escrow;

    @ElementCollection
    @CollectionTable(name = "trade_chat", joinColumns = @JoinColumn(name = "trade_id"))
    private List<ChatMessage> chat = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "createdAt", column = @Column(name = "dispute_created_at")),
            @AttributeOverride(name = "reason", column = @Column(name = "dispute_reason")),
            @AttributeOverride(name = "evidence", column = @Column(name = "dispute_evidence"))
    })
    // private DisputeMetadata disputeMetadata;

    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dispute> disputes = new ArrayList<>();

    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TradeChat> chats = new ArrayList<>();

    @PrePersist
    protected void assignTradeCode() {
        if (tradeCode == null || tradeCode.isEmpty()) {
            tradeCode = SnowflakeIdGenerator.getInstance().nextIdAsString();
        }
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public void setStatus(TradeStatus status) {
        this.status = status;
    }

    public boolean isEscrow() {
        return escrow;
    }

    public void setEscrow(boolean escrow) {
        this.escrow = escrow;
    }

    public List<ChatMessage> getChat() {
        return chat;
    }

    public void setChat(List<ChatMessage> chat) {
        this.chat = chat;
    }

    // public DisputeMetadata getDisputeMetadata() {
    //     return disputeMetadata;
    // }

    // public void setDisputeMetadata(DisputeMetadata disputeMetadata) {
    //     this.disputeMetadata = disputeMetadata;
    // }

    public List<Dispute> getDisputes() {
        return disputes;
    }

    public void setDisputes(List<Dispute> disputes) {
        this.disputes = disputes;
    }

    public List<TradeChat> getChats() {
        return chats;
    }

    public void setChats(List<TradeChat> chats) {
        this.chats = chats;
    }

    @Embeddable
    public static class ChatMessage {
        private Long senderId;
        private String message;
        private LocalDateTime timestamp;

        public Long getSenderId() {
            return senderId;
        }

        public void setSenderId(Long senderId) {
            this.senderId = senderId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }

    // @Embeddable
    // public static class DisputeMetadata {
    //     private LocalDateTime createdAt;
    //     private String reason;
    //     private String evidence;

    //     public LocalDateTime getCreatedAt() {
    //         return createdAt;
    //     }

    //     public void setCreatedAt(LocalDateTime createdAt) {
    //         this.createdAt = createdAt;
    //     }

    //     public String getReason() {
    //         return reason;
    //     }

    //     public void setReason(String reason) {
    //         this.reason = reason;
    //     }

    //     public String getEvidence() {
    //         return evidence;
    //     }

    //     public void setEvidence(String evidence) {
    //         this.evidence = evidence;
    //     }
    // }
}
