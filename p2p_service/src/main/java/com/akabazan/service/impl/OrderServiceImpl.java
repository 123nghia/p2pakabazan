package com.akabazan.service.impl;

import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.UserRepository;
import com.akabazan.common.constant.ErrorCode;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.entity.User;
import com.akabazan.service.OrderService;
import com.akabazan.service.dto.OrderDTO;
import com.akabazan.service.dto.TradeDTO;
import com.akabazan.service.dto.ChatMessageDTO;
import com.akabazan.service.dto.OrderMapper;
import com.akabazan.service.dto.TradeMapper;
import com.akabazan.common.exception.ApplicationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;

    public OrderServiceImpl(UserRepository userRepository,
                            OrderRepository orderRepository,
                            TradeRepository tradeRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
    }

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        User user = getCurrentUser();

        if (user.getKycStatus() != User.KycStatus.VERIFIED) {
            throw new ApplicationException(ErrorCode.KYC_REQUIRED);
        }

        if ("SELL".equalsIgnoreCase(orderDTO.getType())) {
            User.Wallet wallet = user.getWallets().stream()
                    .filter(w -> w.getToken().equalsIgnoreCase(orderDTO.getToken()))
                    .findFirst()
                    .orElseThrow(() -> new ApplicationException(ErrorCode.INSUFFICIENT_BALANCE));

            if (wallet.getBalance() < orderDTO.getAmount()) {
                throw new ApplicationException(ErrorCode.INSUFFICIENT_BALANCE);
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setType(orderDTO.getType().toUpperCase());
        order.setToken(orderDTO.getToken());
        order.setAmount(orderDTO.getAmount());
        order.setPrice(orderDTO.getPrice());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setMinLimit(orderDTO.getMinLimit());
        order.setMaxLimit(orderDTO.getMaxLimit());
        order.setStatus(OrderStatus.OPEN.name());

        return OrderMapper.toDTO(orderRepository.save(order));
    }

    @Override
    public List<OrderDTO> getOrders(String type, String token, String paymentMethod, String sortByPrice) {
        List<Order> orders = orderRepository.findByStatusAndTypeAndTokenAndPaymentMethod(
                OrderStatus.OPEN.name(),
                type != null ? type.toUpperCase() : null,
                token,
                paymentMethod
        );

        if ("asc".equalsIgnoreCase(sortByPrice)) {
            orders.sort((o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice()));
        } else if ("desc".equalsIgnoreCase(sortByPrice)) {
            orders.sort((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));
        }

        return orders.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
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

        return TradeMapper.toDTO(tradeRepository.save(trade));
    }

    private User getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }
}
