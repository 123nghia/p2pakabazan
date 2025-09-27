package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.service.TradeService;
import com.akabazan.service.dto.TradeDTO;
import com.akabazan.service.dto.TradeMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TradeServiceImpl implements TradeService {

    private final EntityManager entityManager;
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final WalletRepository walletRepository;

    public TradeServiceImpl(EntityManager entityManager,
                            OrderRepository orderRepository,
                            TradeRepository tradeRepository,
                            WalletRepository walletRepository) {
        this.entityManager = entityManager;
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
        this.walletRepository = walletRepository;
    }

    @Override
@Transactional
public TradeDTO createTrade(TradeDTO tradeDTO) {
    User buyer = getCurrentUser();

    // Lock order tránh oversell
    Order order = entityManager.find(Order.class, tradeDTO.getOrderId(), LockModeType.PESSIMISTIC_WRITE);
    if (order == null)
        throw new ApplicationException(ErrorCode.ORDER_NOT_FOUND);

    if (!OrderStatus.OPEN.name().equals(order.getStatus()))
        throw new ApplicationException(ErrorCode.ORDER_CLOSED);

    if (tradeDTO.getAmount() < order.getMinLimit() || tradeDTO.getAmount() > order.getMaxLimit())
        throw new ApplicationException(ErrorCode.AMOUNT_OUT_OF_LIMIT);

    if (tradeDTO.getAmount() > order.getAvailableAmount())
        throw new ApplicationException(ErrorCode.INSUFFICIENT_BALANCE);

    // Chỉ giảm availableAmount của order
    order.setAvailableAmount(order.getAvailableAmount() - tradeDTO.getAmount());
    orderRepository.save(order);

    Trade trade = new Trade();
    trade.setOrder(order);
    trade.setBuyer(buyer);
    trade.setSeller(order.getUser());
    trade.setAmount(tradeDTO.getAmount());
    trade.setEscrow("SELL".equalsIgnoreCase(order.getType()));
    trade.setStatus(TradeStatus.PENDING);
    trade.setCreatedAt(LocalDateTime.now());

    return TradeMapper.toDTO(tradeRepository.save(trade));
}


    @Override
    @Transactional
    public TradeDTO confirmPayment(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        if (trade.getStatus() != TradeStatus.PENDING)
            throw new ApplicationException(ErrorCode.INVALID_TRADE_STATUS);

        trade.setStatus(TradeStatus.PAID);
        return TradeMapper.toDTO(tradeRepository.save(trade));
    }

    @Override
    @Transactional
    public TradeDTO confirmReceived(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        if (trade.getStatus() != TradeStatus.PAID)
            throw new ApplicationException(ErrorCode.INVALID_TRADE_STATUS);

        Order order = trade.getOrder();
        User buyer = trade.getBuyer();
        User seller = trade.getSeller();

        // Buyer nhận token
        Wallet buyerWallet = walletRepository.findByUserIdAndToken(buyer.getId(), order.getToken())
                .orElseGet(() -> {
                    Wallet w = new Wallet();
                    w.setUser(buyer);
                    w.setToken(order.getToken());
                    w.setAddress("generated-address-" + buyer.getId());
                    w.setBalance(0.0);
                    w.setAvailableBalance(0.0);
                    return walletRepository.save(w);
                });
        buyerWallet.setBalance(buyerWallet.getBalance() + trade.getAmount());
        buyerWallet.setAvailableBalance(buyerWallet.getAvailableBalance() + trade.getAmount());
        walletRepository.save(buyerWallet);

        // Seller: trừ balance thực (đã escrow khi tạo order)
        Wallet sellerWallet = walletRepository.findByUserIdAndToken(seller.getId(), order.getToken())
                .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));
        sellerWallet.setBalance(sellerWallet.getBalance() - trade.getAmount());
        walletRepository.save(sellerWallet);

        // Hoàn tất trade
        trade.setStatus(TradeStatus.COMPLETED);

        // Nếu order hết hàng thì đóng
        if (order.getAvailableAmount() <= 0)
            order.setStatus(OrderStatus.CLOSED.name());

        orderRepository.save(order);
        return TradeMapper.toDTO(tradeRepository.save(trade));
    }

   


    private User getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = new User();
        u.setId(Long.valueOf(userId));
        return u;
    }
}
