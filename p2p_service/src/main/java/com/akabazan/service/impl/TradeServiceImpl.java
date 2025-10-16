package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.notification.service.NotificationService;
import com.akabazan.repository.FiatAccountRepository;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.TradeChatRepository;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.constant.WalletTransactionType;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.FiatAccount;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.entity.TradeChat;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.service.TradeService;
import com.akabazan.service.WalletTransactionService;
import com.akabazan.service.command.TradeCreateCommand;
import com.akabazan.service.dto.TradeInfoResult;
import com.akabazan.service.dto.TradeMapper;
import com.akabazan.service.dto.TradeResult;
import com.akabazan.service.order.support.SellerFundsManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeServiceImpl implements TradeService {

    private static final Logger log = LoggerFactory.getLogger(TradeServiceImpl.class);
    private static final String INITIAL_CHAT_MESSAGE = "Khởi tạo chát, hai bên trao đôi với nhau";

    private final EntityManager entityManager;
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final TradeChatRepository tradeChatRepository;
    private final WalletRepository walletRepository;
    private final SellerFundsManager sellerFundsManager;
    private final NotificationService notificationService;
    private final FiatAccountRepository fiatAccountRepository;
    private final WalletTransactionService walletTransactionService;
    
    @Value("${app.trade.auto-cancel-minutes:15}")
    private long autoCancelMinutes;
    public TradeServiceImpl(EntityManager entityManager,
            OrderRepository orderRepository,
            TradeRepository tradeRepository,
            TradeChatRepository tradeChatRepository,
            WalletRepository walletRepository,
            SellerFundsManager sellerFundsManager,
            NotificationService  notificationService,
            FiatAccountRepository fiatAccountRepository,
            WalletTransactionService walletTransactionService
            ) {
        this.entityManager = entityManager;
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
        this.tradeChatRepository = tradeChatRepository;
        this.walletRepository = walletRepository;
        this.sellerFundsManager = sellerFundsManager;
        this.notificationService = notificationService;
        this.fiatAccountRepository = fiatAccountRepository;
        this.walletTransactionService = walletTransactionService;
    }

    @Override
    @Transactional
    public TradeResult createTrade(TradeCreateCommand command) {
        log.info("Creating trade for order: {} with amount: {}", command.getOrderId(), command.getAmount());
        
        User actor = getCurrentUser();
        Order order = validateAndLockOrder(command.getOrderId());
        if (order.getUser() != null && order.getUser().getId().equals(actor.getId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }
        validateTradeAmount(command, order);
        
        Trade trade = buildTrade(command, order, actor);
        FiatAccount sellerAccount = resolveSellerAccount(trade.getSeller(), order, command);
        setSellerAccountInfo(trade, sellerAccount);
        
        Trade savedTrade = tradeRepository.save(trade);
        createInitialChat(savedTrade, actor.getId());
        
        log.info("Trade created successfully with ID: {} for order: {}", savedTrade.getId(), command.getOrderId());
        return TradeMapper.toResult(savedTrade);
    }

    private Order validateAndLockOrder(Long orderId) {
        Order order = entityManager.find(Order.class, orderId, LockModeType.PESSIMISTIC_WRITE);
        if (order == null) {
            throw new ApplicationException(ErrorCode.ORDER_NOT_FOUND);
        }
        if (!OrderStatus.OPEN.name().equals(order.getStatus())) {
            throw new ApplicationException(ErrorCode.ORDER_CLOSED);
        }
        return order;
    }

    private void validateTradeAmount(TradeCreateCommand command, Order order) {
        if (command.getAmount() > order.getAvailableAmount()) {
            throw new ApplicationException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        order.setAvailableAmount(order.getAvailableAmount() - command.getAmount());
        orderRepository.save(order);
    }

    private Trade buildTrade(TradeCreateCommand command, Order order, User actor) {
        Trade trade = new Trade();
        trade.setOrder(order);
        trade.setAmount(command.getAmount());
        trade.setStatus(TradeStatus.PENDING);
        trade.setCreatedAt(LocalDateTime.now());
        trade.setTradeCode(String.valueOf(System.currentTimeMillis()));

        TradeParticipants participants = determineTradeParticipants(order, actor, command);
        trade.setBuyer(participants.getBuyer());
        trade.setSeller(participants.getSeller());
        trade.setEscrow(participants.isEscrow());

        return trade;
    }

    private TradeParticipants determineTradeParticipants(Order order, User actor, TradeCreateCommand command) {
        User buyer;
        User seller;
        boolean escrow;

        if ("SELL".equalsIgnoreCase(order.getType())) {
            // Người tạo trade là buyer, seller là chủ order
            buyer = actor;
            seller = order.getUser();
            escrow = true; // coin đã lock từ seller
        } else if ("BUY".equalsIgnoreCase(order.getType())) {
            // Người tạo trade là seller
            seller = actor;
            buyer = order.getUser();
            sellerFundsManager.lock(seller, order.getToken(), command.getAmount());
            escrow = false; // buyer chưa có coin lock
        } else {
            throw new ApplicationException(ErrorCode.INVALID_ORDER_TYPE);
        }

        return new TradeParticipants(buyer, seller, escrow);
    }

    private void setSellerAccountInfo(Trade trade, FiatAccount sellerAccount) {
        if (sellerAccount != null) {
            trade.setSellerFiatAccount(sellerAccount);
            trade.setSellerBankName(sellerAccount.getBankName());
            trade.setSellerAccountNumber(sellerAccount.getAccountNumber());
            trade.setSellerAccountHolder(sellerAccount.getAccountHolder());
            trade.setSellerBankBranch(sellerAccount.getBranch());
            trade.setSellerPaymentType(sellerAccount.getPaymentType());
        }
    }

    // Helper class for trade participants
    private static class TradeParticipants {
        private final User buyer;
        private final User seller;
        private final boolean escrow;

        public TradeParticipants(User buyer, User seller, boolean escrow) {
            this.buyer = buyer;
            this.seller = seller;
            this.escrow = escrow;
        }

        public User getBuyer() { return buyer; }
        public User getSeller() { return seller; }
        public boolean isEscrow() { return escrow; }
    }

    private FiatAccount resolveSellerAccount(User seller, Order order, TradeCreateCommand command) {
        if ("SELL".equalsIgnoreCase(order.getType())) {
            FiatAccount account = order.getFiatAccount();
            if (account != null && !account.getUser().getId().equals(seller.getId())) {
                throw new ApplicationException(ErrorCode.FORBIDDEN);
            }
            return account;
        }

        Long accountId = command.getFiatAccountId();
        if (accountId == null) {
            throw new ApplicationException(ErrorCode.SELLER_PAYMENT_METHOD_REQUIRED);
        }

        FiatAccount account = fiatAccountRepository.findById(accountId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.FIAT_ACCOUNT_NOT_FOUND));

        if (!account.getUser().getId().equals(seller.getId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        return account;
    }


    private void createInitialChat(Trade trade, Long senderId) {
        TradeChat chat = new TradeChat();
        chat.setTrade(trade);
        chat.setSenderId(senderId);
        chat.setMessage(INITIAL_CHAT_MESSAGE);
        chat.setTimestamp(LocalDateTime.now());
        tradeChatRepository.save(chat);
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
        double buyerBalanceBefore = buyerWallet.getBalance();
        double buyerAvailableBefore = buyerWallet.getAvailableBalance();
        buyerWallet.setBalance(buyerBalanceBefore + trade.getAmount());
        buyerWallet.setAvailableBalance(buyerAvailableBefore + trade.getAmount());
        walletRepository.save(buyerWallet);
        walletTransactionService.record(
                buyerWallet,
                WalletTransactionType.CREDIT,
                trade.getAmount(),
                buyerBalanceBefore,
                buyerWallet.getBalance(),
                buyerAvailableBefore,
                buyerWallet.getAvailableBalance(),
                buyer.getId(),
                "TRADE_COMPLETED",
                trade.getId(),
                "Buyer receives tokens");
        // Seller: trừ balance thực và cập nhật lại lượng lock
        Wallet sellerWallet = walletRepository.findByUserIdAndToken(seller.getId(), order.getToken())
                .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));
        double sellerBalanceBefore = sellerWallet.getBalance();
        double sellerAvailableBefore = sellerWallet.getAvailableBalance();
        sellerWallet.setBalance(sellerBalanceBefore - trade.getAmount());
        walletRepository.save(sellerWallet);
        walletTransactionService.record(
                sellerWallet,
                WalletTransactionType.DEBIT,
                trade.getAmount(),
                sellerBalanceBefore,
                sellerWallet.getBalance(),
                sellerAvailableBefore,
                sellerWallet.getAvailableBalance(),
                seller.getId(),
                "TRADE_COMPLETED",
                trade.getId(),
                "Seller releases tokens");

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
        try {
            return entityManager.getReference(User.class, Long.valueOf(userId));
        } catch (EntityNotFoundException ex) {
            throw new ApplicationException(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public TradeResult cancelTrade(Long tradeId) {
        User currentUser = getCurrentUser();
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));
        return cancelTradeInternal(trade, currentUser.getId(), true);
    }

    @Override
    @Transactional
    public int autoCancelExpiredTrades() {
        log.debug("Starting auto-cancel expired trades process");
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(autoCancelMinutes);
        List<Trade> expiredTrades = tradeRepository.findByStatusAndCreatedAtBefore(TradeStatus.PENDING, threshold);
        
        log.info("Found {} expired trades to cancel", expiredTrades.size());
        int cancelled = 0;
        for (Trade trade : expiredTrades) {
            if (trade.getStatus() != TradeStatus.PENDING) {
                log.debug("Skipping trade {} - status is not PENDING", trade.getId());
                continue;
            }
            log.info("Auto-cancelling expired trade: {}", trade.getId());
            cancelTradeInternal(trade, trade.getSeller().getId(), false);
            cancelled++;
        }
        
        log.info("Auto-cancel process completed. Cancelled {} trades", cancelled);
        return cancelled;
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
                .map(trade -> {
                    TradeResult result = TradeMapper.toResult(trade);
                    boolean isBuyer = trade.getBuyer().getId().equals(userId);
                    boolean isSeller = trade.getSeller().getId().equals(userId);
                    if (isBuyer) {
                        result.setRole("BUYER");
                        result.setCounterparty(trade.getSeller().getEmail());
                    } else if (isSeller) {
                        result.setRole("SELLER");
                        result.setCounterparty(trade.getBuyer().getEmail());
                    }

                    String orderType = trade.getOrder().getType();
                    Long creatorId = "SELL".equalsIgnoreCase(orderType)
                            ? trade.getBuyer().getId()
                            : trade.getSeller().getId();
                    boolean canCancel = trade.getStatus() == TradeStatus.PENDING
                            && creatorId.equals(userId);
                    result.setCanCancel(canCancel);
                    return result;
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

        if (t.getSellerFiatAccount() != null) {
            r.setSellerFiatAccountId(t.getSellerFiatAccount().getId());
        }
        r.setBankName(t.getSellerBankName());
        r.setAccountNumber(t.getSellerAccountNumber());
        r.setAccountHolder(t.getSellerAccountHolder());
        r.setBankBranch(t.getSellerBankBranch());
        r.setPaymentType(t.getSellerPaymentType());

        boolean canCancel = t.getStatus() == TradeStatus.PENDING;
         r.setCanCancel(canCancel);


        var orderInfor = t.getOrder();

        if(orderInfor != null)
        {
            r.setPrice(orderInfor.getPrice());
          
        }

        
        return r;
    }

    private TradeResult cancelTradeInternal(Trade trade, Long actorId, boolean enforceCreator) {
        String orderType = trade.getOrder().getType();
        Long creatorId = "SELL".equalsIgnoreCase(orderType)
                ? trade.getBuyer().getId()
                : trade.getSeller().getId();

        if (enforceCreator && (actorId == null || !creatorId.equals(actorId))) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        if (trade.getStatus() != TradeStatus.PENDING) {
            throw new ApplicationException(ErrorCode.INVALID_TRADE_STATUS);
        }

        double refundAmount = trade.getAmount();

        Wallet sellerWallet = walletRepository.findByUserIdAndToken(
                        trade.getSeller().getId(),
                        trade.getOrder().getToken())
                .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));

        double balanceBefore = sellerWallet.getBalance();
        double availableBefore = sellerWallet.getAvailableBalance();
        sellerWallet.setAvailableBalance(availableBefore + refundAmount);
        walletRepository.save(sellerWallet);

        Long performerId = actorId != null ? actorId : trade.getSeller().getId();

        walletTransactionService.record(
                sellerWallet,
                WalletTransactionType.UNLOCK,
                refundAmount,
                balanceBefore,
                sellerWallet.getBalance(),
                availableBefore,
                sellerWallet.getAvailableBalance(),
                performerId,
                "TRADE_CANCELLED",
                trade.getId(),
                "Cancel trade and unlock funds");

        Order order = trade.getOrder();
        order.setAvailableAmount(order.getAvailableAmount() + refundAmount);
        orderRepository.save(order);

        trade.setStatus(TradeStatus.CANCELLED);
        tradeRepository.save(trade);

        return TradeMapper.toResult(trade);
    }
}
