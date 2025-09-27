package com.akabazan.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.akabazan.repository.constant.TradeStatus;

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


    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dispute> disputes = new ArrayList<>();

    // Getter/Setter
    public List<Dispute> getDisputes() { return disputes; }
    public void setDisputes(List<Dispute> disputes) { this.disputes = disputes; }
 

    // Getters & Setters
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
    public boolean isEscrow() { return escrow; }
    public void setEscrow(boolean escrow) { this.escrow = escrow; }
    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true)
private List<TradeChat> chats = new ArrayList<>();

public List<TradeChat> getChats() { return chats; }
public void setChats(List<TradeChat> chats) { this.chats = chats; }



    // Embeddable classes
    @Embeddable
    public static class ChatMessage {
        private Long senderId;
        private String message;
        private LocalDateTime timestamp;

        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }


}
