package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.notification.service.NotificationService;
import com.akabazan.repository.FiatAccountRepository;
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
import jakarta.persistence.EntityNotFoundException;
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
    private final FiatAccountRepository fiatAccountRepository;
       @Value("${app.trade.auto-cancel-minutes:15}")
    private long autoCancelMinutes;
    public TradeServiceImpl(EntityManager entityManager,
            OrderRepository orderRepository,
            TradeRepository tradeRepository,
            WalletRepository walletRepository,
            SellerFundsManager sellerFundsManager,
            NotificationService  notificationService,
            FiatAccountRepository fiatAccountRepository
            ) {
        this.entityManager = entityManager;
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
        this.walletRepository = walletRepository;
        this.sellerFundsManager = sellerFundsManager;
        this.notificationService = notificationService;
        this.fiatAccountRepository = fiatAccountRepository;
    }

    @Override
    @Transactional
    public TradeResult createTrade(TradeCreateCommand command) {
        User actor = getCurrentUser();

        // Lock order tránh oversell
        Order order = entityManager.find(Order.class, command.getOrderId(), LockModeType.PESSIMISTIC_WRITE);
        if (order == null)
            throw new ApplicationException(ErrorCode.ORDER_NOT_FOUND);

        if (!OrderStatus.OPEN.name().equals(order.getStatus()))
            throw new ApplicationException(ErrorCode.ORDER_CLOSED);

        // if (command.getAmount() < order.getMinLimit() || command.getAmount() > order.getMaxLimit())
        //     throw new ApplicationException(ErrorCode.AMOUNT_OUT_OF_LIMIT);

        if (command.getAmount() > order.getAvailableAmount())
            throw new ApplicationException(ErrorCode.INSUFFICIENT_BALANCE);

        order.setAvailableAmount(order.getAvailableAmount() - command.getAmount());
        orderRepository.save(order);
        Trade trade = new Trade();
        trade.setOrder(order);

        User buyer;
        User seller;

        if ("SELL".equalsIgnoreCase(order.getType())) {
            // Người tạo trade là buyer, seller là chủ order
            buyer = actor;
            seller = order.getUser();
            trade.setEscrow(true); // coin đã lock từ seller
        } else if ("BUY".equalsIgnoreCase(order.getType())) {
            // Người tạo trade là seller
            seller = actor;
            buyer = order.getUser();
            sellerFundsManager.lock(seller, order.getToken(), command.getAmount());
            trade.setEscrow(false); // buyer chưa có coin lock
        } else {
            throw new ApplicationException(ErrorCode.INVALID_ORDER_TYPE);
        }

        trade.setBuyer(buyer);
        trade.setSeller(seller);

        FiatAccount sellerAccount = resolveSellerAccount(seller, order, command);
        
        trade.setAmount(command.getAmount());
        trade.setStatus(TradeStatus.PENDING);
        trade.setCreatedAt(LocalDateTime.now());
        trade.setTradeCode( String.valueOf(System.currentTimeMillis()));

        if (sellerAccount != null) {
            trade.setSellerFiatAccount(sellerAccount);
            trade.setSellerBankName(sellerAccount.getBankName());
            trade.setSellerAccountNumber(sellerAccount.getAccountNumber());
            trade.setSellerAccountHolder(sellerAccount.getAccountHolder());
            trade.setSellerBankBranch(sellerAccount.getBranch());
            trade.setSellerPaymentType(sellerAccount.getPaymentType());
        }

        // ✅ Lưu vào DB
        tradeRepository.save(trade);
        return TradeMapper.toResult(trade);
    }

    private FiatAccount resolveSellerAccount(User seller, Order order, TradeCreateCommand command) {
        Long existingAccountId = command.getFiatAccountId();
        if (existingAccountId != null) {
            FiatAccount account = fiatAccountRepository.findById(existingAccountId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.FIAT_ACCOUNT_NOT_FOUND));
            if (!account.getUser().getId().equals(seller.getId())) {
                throw new ApplicationException(ErrorCode.FORBIDDEN);
            }
            return account;
        }

        if ("SELL".equalsIgnoreCase(order.getType())) {
            if (hasSellerAccountInput(command)) {
                return findOrCreateSellerAccount(seller, command);
            }
            FiatAccount account = order.getFiatAccount();
            if (account == null) {
                throw new ApplicationException(ErrorCode.SELLER_PAYMENT_METHOD_REQUIRED);
            }
            return account;
        }

        return findOrCreateSellerAccount(seller, command);
    }

    private FiatAccount findOrCreateSellerAccount(User seller, TradeCreateCommand command) {
        if (!hasSellerAccountInput(command)) {
            throw new ApplicationException(ErrorCode.SELLER_PAYMENT_METHOD_REQUIRED);
        }

        User sellerRef = entityManager.getReference(User.class, seller.getId());
        return fiatAccountRepository.findByUserAndBankNameAndAccountNumberAndAccountHolder(
                        sellerRef,
                        command.getBankName(),
                        command.getAccountNumber(),
                        command.getAccountHolder())
                .map(account -> updateSellerAccount(account, command))
                .orElseGet(() -> createSellerAccount(sellerRef, command));
    }

    private FiatAccount updateSellerAccount(FiatAccount account, TradeCreateCommand command) {
        account.setBranch(command.getBranch());
        account.setPaymentType(command.getPaymentType());
        return fiatAccountRepository.save(account);
    }

    private FiatAccount createSellerAccount(User seller, TradeCreateCommand command) {
        FiatAccount account = new FiatAccount();
        account.setUser(seller);
        account.setBankName(command.getBankName());
        account.setAccountNumber(command.getAccountNumber());
        account.setAccountHolder(command.getAccountHolder());
        account.setBranch(command.getBranch());
        account.setPaymentType(command.getPaymentType());
        return fiatAccountRepository.save(account);
    }

    private boolean hasSellerAccountInput(TradeCreateCommand command) {
        return isNotBlank(command.getBankName())
                && isNotBlank(command.getAccountNumber())
                && isNotBlank(command.getAccountHolder())
                && isNotBlank(command.getPaymentType());
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
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

        String orderType = trade.getOrder().getType();
          Long creatorId = "SELL".equalsIgnoreCase(orderType)
            ? trade.getBuyer().getId() // người tạo order
            : trade.getSeller().getId(); // người tạo order nếu là BUY

        if (!creatorId.equals(currentUser.getId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        if (trade.getStatus() != TradeStatus.PENDING) {
            throw new ApplicationException(ErrorCode.INVALID_TRADE_STATUS);
        }

        double refundAmount = trade.getAmount();


      if (currentUser.getId().equals(trade.getSeller().getId())) {
                // 1. Hoàn coin lại cho seller (unlock funds)
                Wallet sellerWallet = walletRepository.findByUserIdAndToken(
                trade.getSeller().getId(),
                trade.getOrder().getToken()).orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));

                sellerWallet.setAvailableBalance(
                sellerWallet.getAvailableBalance() + refundAmount);
                walletRepository.save(sellerWallet);
        }

        

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
}
