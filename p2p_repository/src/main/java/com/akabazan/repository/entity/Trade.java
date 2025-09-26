package com.akabazan.repository.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.akabazan.repository.constant.*;


@Entity
@Table(name = "trades")
public class Trade extends AbstractEntity { // Extend AbstractEntity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TradeStatus status = TradeStatus.PENDING;

    @ElementCollection
    @CollectionTable(name = "trade_chat", joinColumns = @JoinColumn(name = "trade_id"))
    private List<ChatMessage> chat = new ArrayList<>();

    @Column(name = "escrow", nullable = false)
    private boolean escrow;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "dispute_created_at")),
        @AttributeOverride(name = "reason", column = @Column(name = "dispute_reason")),
        @AttributeOverride(name = "evidence", column = @Column(name = "dispute_evidence"))
    })
    private Dispute dispute;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }
    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public TradeStatus getStatus() { return status; }
    public void setStatus(TradeStatus status) { this.status = status; }
    public List<ChatMessage> getChat() { return chat; }
    public void setChat(List<ChatMessage> chat) { this.chat = chat; }
    public boolean isEscrow() { return escrow; }
    public void setEscrow(boolean escrow) { this.escrow = escrow; }

    public Dispute getDispute() { return dispute; }
    public void setDispute(Dispute dispute) { this.dispute = dispute; }


    @Embeddable
    
    public static class ChatMessage {
        private Long senderId;
        private String message;
        private LocalDateTime timestamp;

        // Getters and Setters
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    @Embeddable
    public static class Dispute {
        private String reason;
        private String evidence;
        private LocalDateTime createdAt;

        // Getters and Setters
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getEvidence() { return evidence; }
        public void setEvidence(String evidence) { this.evidence = evidence; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}