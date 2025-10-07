package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.common.util.SnowflakeIdGenerator;
import com.akabazan.notification.service.NotificationService;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.FiatAccount;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.service.TradeService;
import com.akabazan.service.command.TradeCreateCommand;
import com.akabazan.service.dto.TradeInfoResult;
import com.akabazan.service.dto.TradeMapper;
import com.akabazan.service.dto.TradeResult;
import com.akabazan.service.order.support.SellerFundsManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeServiceImpl implements TradeService {

    private final EntityManager entityManager;
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final WalletRepository walletRepository;
    private final SellerFundsManager sellerFundsManager;
   private final NotificationService notificationService;
       @Value("${app.trade.auto-cancel-minutes:15}")
    private long autoCancelMinutes;
    public TradeServiceImpl(EntityManager entityManager,
            OrderRepository orderRepository,
            TradeRepository tradeRepository,
            WalletRepository walletRepository,
            SellerFundsManager sellerFundsManager,
            NotificationService  notificationService
            ) {
        this.entityManager = entityManager;
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
        this.walletRepository = walletRepository;
        this.sellerFundsManager = sellerFundsManager;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public TradeResult createTrade(TradeCreateCommand command) {
        User buyer = getCurrentUser();

        // Lock order tránh oversell
        Order order = entityManager.find(Order.class, command.getOrderId(), LockModeType.PESSIMISTIC_WRITE);
        if (order == null)
            throw new ApplicationException(ErrorCode.ORDER_NOT_FOUND);

        if (!OrderStatus.OPEN.name().equals(order.getStatus()))
            throw new ApplicationException(ErrorCode.ORDER_CLOSED);

        if (command.getAmount() < order.getMinLimit() || command.getAmount() > order.getMaxLimit())
            throw new ApplicationException(ErrorCode.AMOUNT_OUT_OF_LIMIT);

        if (command.getAmount() > order.getAvailableAmount())
            throw new ApplicationException(ErrorCode.INSUFFICIENT_BALANCE);

        order.setAvailableAmount(order.getAvailableAmount() - command.getAmount());
        orderRepository.save(order);
        Trade trade = new Trade();
        trade.setOrder(order);
        
        if ("SELL".equalsIgnoreCase(order.getType())) {
            // Người tạo trade là buyer
            trade.setBuyer(buyer);
            trade.setSeller(order.getUser());
            trade.setEscrow(true); // coin đã lock từ seller

        } else if ("BUY".equalsIgnoreCase(order.getType())) {
            // Người tạo trade là seller
            trade.setSeller(buyer);
            trade.setBuyer(order.getUser());
            sellerFundsManager.lock(buyer, order.getToken(), command.getAmount());
            trade.setEscrow(false); // buyer chưa có coin lock
        }
        
        trade.setAmount(command.getAmount());
        trade.setStatus(TradeStatus.PENDING);
        trade.setCreatedAt(LocalDateTime.now());
        trade.setTradeCode( String.valueOf(System.currentTimeMillis()));
        // ✅ Lưu vào DB
        tradeRepository.save(trade);
        return TradeMapper.toResult(trade);
    }
    @Override
    @Transactional
    public TradeResult confirmPayment(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));
        User buyer = getCurrentUser();
        if (!trade.getBuyer().getId().equals(buyer.getId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }

        if (trade.getStatus() != TradeStatus.PENDING)
            throw new ApplicationException(ErrorCode.INVALID_TRADE_STATUS);
        trade.setStatus(TradeStatus.PAID);
        return TradeMapper.toResult(tradeRepository.save(trade));
    }

    @Override
    @Transactional
    public TradeResult confirmReceived(Long tradeId) {
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
        // Seller: trừ balance thực và cập nhật lại lượng lock
        Wallet sellerWallet = walletRepository.findByUserIdAndToken(seller.getId(), order.getToken())
                .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));
        sellerWallet.setBalance(sellerWallet.getBalance() - trade.getAmount());
        // sellerWallet.setAvailableBalance(sellerWallet.getAvailableBalance() -
        // trade.getAmount());
        walletRepository.save(sellerWallet);

        // Hoàn tất trade
        trade.setStatus(TradeStatus.COMPLETED);
        tradeRepository.save(trade);

        if (order.getAvailableAmount() <= 0) {
            order.setAvailableAmount(0.0);
            boolean allTradesCompleted = tradeRepository
                    .findByOrderId(order.getId())
                    .stream()
                    .allMatch(t -> t.getStatus() == TradeStatus.COMPLETED
                            || t.getStatus() == TradeStatus.CANCELLED);
            if (allTradesCompleted) {
                order.setStatus(OrderStatus.CLOSED.name());
            } else {
            }
        }
        orderRepository.save(order);
        return TradeMapper.toResult(trade);
    }

    private User getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = new User();
        u.setId(Long.valueOf(userId));
        return u;
    }

    @Override
    @Transactional
    public TradeResult cancelTrade(Long tradeId) {
        User currentUser = getCurrentUser();

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("Trade not found"));

        // Buyer hoặc Seller đều có quyền huỷ
        if (!trade.getBuyer().getId().equals(currentUser.getId()) &&
                !trade.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to cancel this trade");
        }

        // Chỉ huỷ được khi đang PENDING
        if (trade.getStatus() != TradeStatus.PENDING) {
            throw new RuntimeException("Trade cannot be canceled at this stage");
        }

        double refundAmount = trade.getAmount();

        // 1. Hoàn coin lại cho seller (unlock funds)
        Wallet sellerWallet = walletRepository.findByUserIdAndToken(
                trade.getSeller().getId(),
                trade.getOrder().getToken()).orElseThrow(() -> new RuntimeException("Seller wallet not found"));

        sellerWallet.setAvailableBalance(
                sellerWallet.getAvailableBalance() + refundAmount);
        walletRepository.save(sellerWallet);

        // 2. Hoàn lại availableAmount trong order
        Order order = trade.getOrder();
        order.setAvailableAmount(order.getAvailableAmount() + refundAmount);
        orderRepository.save(order);

        // 3. Cập nhật trạng thái trade
        trade.setStatus(TradeStatus.CANCELLED);
        tradeRepository.save(trade);
        return TradeMapper.toResult(trade);
    }

    @Override
    @Transactional
    public List<TradeResult> getTradesByOrder(Long orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.ORDER_NOT_FOUND));

        return tradeRepository.findByOrderId(orderId)
                .stream()
                .map(TradeMapper::toResult)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<TradeResult> getTradesByUser(Long userId) {
        return tradeRepository.findByUser(userId)
                .stream()
               .map(t -> {
                TradeResult r = TradeMapper.toResult(t);
                // ✅ Thêm vai trò dựa trên userId hiện tại
                if (t.getBuyer().getId().equals(userId)) {
                    r.setRole("BUYER");
                } else if (t.getSeller().getId().equals(userId)) {
                    r.setRole("SELLER");
                }
                boolean canCancel = t.getStatus() == TradeStatus.PENDING;
                r.setCanCancel(canCancel);

                return r;
            })
                .collect(Collectors.toList());
    }



    @Override
    @Transactional
    public TradeInfoResult getTradeInfo(Long tradeId) {
        Trade t = tradeRepository.findById(tradeId)
            .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        // Chỉ buyer/seller của trade mới được xem
        User current = getCurrentUser();
        if (!t.getBuyer().getId().equals(current.getId()) && !t.getSeller().getId().equals(current.getId())) {
              throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        TradeInfoResult r = new TradeInfoResult();
        r.setTradeId(t.getId());
        r.setTradeCode(t.getTradeCode());
        r.setOrderType(t.getOrder().getType());     // "BUY"/"SELL"
        r.setStatus(t.getStatus().name());          // enum -> string
        r.setAmount(t.getAmount());

        if (t.getStatus() == TradeStatus.PENDING) {
            LocalDateTime autoCancelAt = t.getCreatedAt().plusMinutes(autoCancelMinutes);
            long remain = Math.max(0, Duration.between(LocalDateTime.now(), autoCancelAt).getSeconds());
            r.setAutoCancelAt(autoCancelAt);
            r.setTimeRemainingSeconds(remain);
        } else {
            r.setAutoCancelAt(null);
            r.setTimeRemainingSeconds(0L);
        }

        // 👇 Thêm logic xác định vai trò
        if (t.getBuyer().getId().equals(current.getId())) {
        r.setRole("BUYER");
        } else if (t.getSeller().getId().equals(current.getId())) {
        r.setRole("SELLER");
        }

        FiatAccount fa = t.getOrder().getFiatAccount();
        if (fa != null) {
        r.setBankName(fa.getBankName());
        r.setAccountNumber(fa.getAccountNumber());
        r.setAccountHolder(fa.getAccountHolder());
    
        }

        boolean canCancel = t.getStatus() == TradeStatus.PENDING;
         r.setCanCancel(canCancel);


        var orderInfor = t.getOrder();

        if(orderInfor != null)
        {
            r.setPrice(orderInfor.getPrice());
          
        }

        
        return r;
    }
}
