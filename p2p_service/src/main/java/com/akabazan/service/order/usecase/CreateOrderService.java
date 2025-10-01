package com.akabazan.service.order.usecase;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.FiatAccountRepository;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.entity.FiatAccount;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.User;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.dto.OrderResult;
import com.akabazan.service.dto.OrderMapper;
import com.akabazan.service.order.support.SellerFundsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CreateOrderService implements CreateOrderUseCase {

    private static final double DEFAULT_MIN_LIMIT = 10.0;
    private static final double DEFAULT_MAX_LIMIT = 100.0;
    private static final long ORDER_EXPIRATION_MINUTES = 15L;

    private final CurrentUserService currentUserService;
    private final FiatAccountRepository fiatAccountRepository;
    private final OrderRepository orderRepository;
    private final SellerFundsManager sellerFundsManager;

    public CreateOrderService(CurrentUserService currentUserService,
                              FiatAccountRepository fiatAccountRepository,
                              OrderRepository orderRepository,
                              SellerFundsManager sellerFundsManager) {
        this.currentUserService = currentUserService;
        this.fiatAccountRepository = fiatAccountRepository;
        this.orderRepository = orderRepository;
        this.sellerFundsManager = sellerFundsManager;
    }

    @Override
    public OrderResult create(OrderResult orderResult) {
        User user = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        ensureKycVerified(user);
        applyDefaultLimits(orderResult);

        String orderType = normalizeOrderType(orderResult.getType());
        FiatAccount fiatAccount = resolveFiatAccount(user, orderResult);

        if (isSellOrder(orderType)) {
            sellerFundsManager.lock(user, orderResult.getToken(), orderResult.getAmount());
        }

        Order savedOrder = orderRepository.save(buildOrder(orderResult, user, orderType, fiatAccount));

        return enrichWithFiatAccount(OrderMapper.toResult(savedOrder), fiatAccount);
    }

    private void ensureKycVerified(User user) {
        if (user.getKycStatus() != User.KycStatus.VERIFIED) {
            throw new ApplicationException(ErrorCode.KYC_REQUIRED);
        }
    }

    private void applyDefaultLimits(OrderResult orderResult) {
        if (orderResult.getMinLimit() == null) {
            orderResult.setMinLimit(DEFAULT_MIN_LIMIT);
        }
        if (orderResult.getMaxLimit() == null) {
            orderResult.setMaxLimit(DEFAULT_MAX_LIMIT);
        }
    }

    private String normalizeOrderType(String type) {
        String normalized = type == null ? "SELL" : type.trim().toUpperCase();
        if (!"BUY".equals(normalized) && !"SELL".equals(normalized)) {
            throw new ApplicationException(ErrorCode.INVALID_ORDER_TYPE);
        }
        return normalized;
    }

    private FiatAccount resolveFiatAccount(User user, OrderResult orderResult) {
        return fiatAccountRepository
                .findByUserAndBankNameAndAccountNumberAndAccountHolder(
                        user,
                        orderResult.getBankName(),
                        orderResult.getBankAccount(),
                        orderResult.getAccountHolder()
                )
                .orElseGet(() -> {
                    FiatAccount account = new FiatAccount();
                    account.setUser(user);
                    account.setBankName(orderResult.getBankName());
                    account.setAccountNumber(orderResult.getBankAccount());
                    account.setAccountHolder(orderResult.getAccountHolder());
                    account.setPaymentType(orderResult.getPaymentMethod());
                    return fiatAccountRepository.save(account);
                });
    }

    private Order buildOrder(OrderResult orderResult, User user, String orderType, FiatAccount fiatAccount) {
        Order order = new Order();
        order.setUser(user);
        order.setType(orderType);
        order.setToken(orderResult.getToken());
        order.setAmount(orderResult.getAmount());
        order.setAvailableAmount(orderResult.getAmount());
        order.setPrice(orderResult.getPrice());
        order.setMinLimit(orderResult.getMinLimit());
        order.setMaxLimit(orderResult.getMaxLimit());
        order.setPaymentMethod(orderResult.getPaymentMethod());
        order.setFiatAccount(fiatAccount);
        order.setStatus(OrderStatus.OPEN.name());
        order.setExpireAt(LocalDateTime.now().plusMinutes(ORDER_EXPIRATION_MINUTES));
        return order;
    }

    private OrderResult enrichWithFiatAccount(OrderResult dto, FiatAccount fiatAccount) {
        dto.setFiatAccountId(fiatAccount.getId());
        dto.setBankName(fiatAccount.getBankName());
        dto.setBankAccount(fiatAccount.getAccountNumber());
        dto.setAccountHolder(fiatAccount.getAccountHolder());
        return dto;
    }

    private boolean isSellOrder(String orderType) {
        return "SELL".equalsIgnoreCase(orderType);
    }
}
