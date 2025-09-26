package com.akabazan.service.impl;

import com.akabazan.repository.*;
import com.akabazan.repository.constant.*;
import com.akabazan.repository.entity.*;
import com.akabazan.service.TradeService;
import com.akabazan.service.dto.*;
import com.akabazan.common.exception.*;
import com.akabazan.common.constant.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TradeServiceImpl implements TradeService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;

    public TradeServiceImpl(UserRepository userRepository,
                            OrderRepository orderRepository,
                            TradeRepository tradeRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
    }

    @Override
    public TradeDTO createTrade(TradeDTO tradeDTO) {
        User buyer = getCurrentUser();
        Order order = orderRepository.findById(tradeDTO.getOrderId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.ORDER_NOT_FOUND));

        if (!OrderStatus.OPEN.name().equals(order.getStatus())) {
            throw new ApplicationException(ErrorCode.ORDER_CLOSED);
        }

        if (tradeDTO.getAmount() < order.getMinLimit() || tradeDTO.getAmount() > order.getMaxLimit()) {
            throw new ApplicationException(ErrorCode.AMOUNT_OUT_OF_LIMIT);
        }

        User seller = order.getUser();

        if ("SELL".equalsIgnoreCase(order.getType())) {
            User.Wallet wallet = seller.getWallets().stream()
                    .filter(w -> w.getToken().equalsIgnoreCase(order.getToken()))
                    .findFirst()
                    .orElseThrow(() -> new ApplicationException(ErrorCode.INSUFFICIENT_BALANCE));

            if (wallet.getBalance() < tradeDTO.getAmount()) {
                throw new ApplicationException(ErrorCode.INSUFFICIENT_BALANCE);
            }

            wallet.setBalance(wallet.getBalance() - tradeDTO.getAmount()); // Escrow
            userRepository.save(seller);
        }

        Trade trade = new Trade();
        trade.setOrder(order);
        trade.setBuyer(buyer);
        trade.setSeller(seller);
        trade.setAmount(tradeDTO.getAmount());
        trade.setEscrow("SELL".equalsIgnoreCase(order.getType()));
        trade.setStatus(TradeStatus.PENDING);

        return TradeMapper.toDTO(tradeRepository.save(trade));
    }

    @Override
    public TradeDTO confirmPayment(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        if (trade.getStatus() != TradeStatus.PENDING) {
            throw new ApplicationException(ErrorCode.INVALID_TRADE_STATUS);
        }

        trade.setStatus(TradeStatus.PAID);
        return TradeMapper.toDTO(tradeRepository.save(trade));
    }

    @Override
    public TradeDTO confirmReceived(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        if (trade.getStatus() != TradeStatus.PAID) {
            throw new ApplicationException(ErrorCode.INVALID_TRADE_STATUS);
        }

        Order order = trade.getOrder();
        User buyer = trade.getBuyer();

        User.Wallet buyerWallet = buyer.getWallets().stream()
                .filter(w -> w.getToken().equalsIgnoreCase(order.getToken()))
                .findFirst()
                .orElseGet(() -> {
                    User.Wallet newWallet = new User.Wallet();
                    newWallet.setToken(order.getToken());
                    newWallet.setAddress("generated-address-" + buyer.getId());
                    newWallet.setBalance(0.0);
                    buyer.getWallets().add(newWallet);
                    return newWallet;
                });

        buyerWallet.setBalance(buyerWallet.getBalance() + trade.getAmount());
        userRepository.save(buyer);

        trade.setStatus(TradeStatus.COMPLETED);
        order.setAmount(order.getAmount() - trade.getAmount());

        if (order.getAmount() <= 0) {
            order.setStatus(OrderStatus.CLOSED.name());
        }

        orderRepository.save(order);
        return TradeMapper.toDTO(tradeRepository.save(trade));
    }

    @Override
    public TradeDTO sendChatMessage(Long tradeId, ChatMessageDTO messageDTO) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        String[] suspiciousKeywords = {"scam", "otp", "wrong transfer"};
        for (String keyword : suspiciousKeywords) {
            if (messageDTO.getMessage().toLowerCase().contains(keyword)) {
                System.out.println("Suspicious message detected: " + messageDTO.getMessage());
            }
        }

        Trade.ChatMessage chatMessage = new Trade.ChatMessage();
        chatMessage.setSenderId(getCurrentUser().getId());
        chatMessage.setMessage(messageDTO.getMessage());
        chatMessage.setTimestamp(LocalDateTime.now());

        trade.getChat().add(chatMessage);
        return TradeMapper.toDTO(tradeRepository.save(trade));
    }

    @Override
    public TradeDTO openDispute(Long tradeId, String reason, String evidence) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        if (trade.getStatus() == TradeStatus.DISPUTED) {
            throw new ApplicationException(ErrorCode.ALREADY_IN_DISPUTE);
        }

        trade.setStatus(TradeStatus.DISPUTED);
        Trade.Dispute dispute = new Trade.Dispute();
        dispute.setReason(reason);
        dispute.setEvidence(evidence);
        dispute.setCreatedAt(LocalDateTime.now());
        trade.setDispute(dispute);

        System.out.println("Dispute opened: Trade ID " + tradeId + ", Reason: " + reason);
        return TradeMapper.toDTO(tradeRepository.save(trade));
    }

    private User getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }
}
