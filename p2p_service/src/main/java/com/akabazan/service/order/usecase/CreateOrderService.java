package com.akabazan.service.order.usecase;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.constant.NotificationConstants;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.notification.enums.NotificationType;
import com.akabazan.notification.service.NotificationService;
import com.akabazan.repository.FiatAccountRepository;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.entity.FiatAccount;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.User;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.command.OrderCreateCommand;
import com.akabazan.service.dto.OrderMapper;
import com.akabazan.service.dto.OrderResult;
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
    private final NotificationService notificationService;

    private final CurrentUserService currentUserService;
    private final FiatAccountRepository fiatAccountRepository;
    private final OrderRepository orderRepository;
    private final SellerFundsManager sellerFundsManager;

    public CreateOrderService(CurrentUserService currentUserService,
            FiatAccountRepository fiatAccountRepository,
            OrderRepository orderRepository,
            SellerFundsManager sellerFundsManager,
            NotificationService notificationService) {
        this.currentUserService = currentUserService;
        this.fiatAccountRepository = fiatAccountRepository;
        this.orderRepository = orderRepository;
        this.sellerFundsManager = sellerFundsManager;
        this.notificationService = notificationService;
    }

    @Override
    public OrderResult create(OrderCreateCommand command) {
        User user = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        ensureKycVerified(user);
        validateInput(command);
        applyDefaultLimits(command);

        String orderType = normalizeOrderType(command.getType());
        FiatAccount fiatAccount = resolveFiatAccount(user, command);

        if (fiatAccount == null && (command.getPaymentMethod() == null || command.getPaymentMethod().isBlank())) {
            throw new ApplicationException(ErrorCode.INVALID_FIAT_ACCOUNT_INPUT);
        }

        Order savedOrder = orderRepository.save(buildOrder(command, user, orderType, fiatAccount));

        if (isSellOrder(orderType)) {
            sellerFundsManager.lockForSellOrder(savedOrder);
            orderRepository.save(savedOrder);
        }
        String typeDisplay = "BUY".equals(orderType) ? NotificationConstants.BUY : NotificationConstants.SELL;
        notificationService.notifyUser(user.getId(), NotificationType.ORDER_CREATED,
                String.format(NotificationConstants.ORDER_CREATED_SUCCESS, typeDisplay));
        return enrichWithFiatAccount(OrderMapper.toResult(savedOrder), fiatAccount);
    }

    private void ensureKycVerified(User user) {
        if (user.getKycStatus() != User.KycStatus.VERIFIED) {
            throw new ApplicationException(ErrorCode.KYC_REQUIRED);
        }
    }

    private void applyDefaultLimits(OrderCreateCommand command) {
        if (command.getMinLimit() == null) {
            command.setMinLimit(DEFAULT_MIN_LIMIT);
        }
        if (command.getMaxLimit() == null) {
            command.setMaxLimit(DEFAULT_MAX_LIMIT);
        }
    }

    private String normalizeOrderType(String type) {
        if (type == null) {
            throw new ApplicationException(ErrorCode.INVALID_ORDER_TYPE);
        }
        String normalized = type.trim().toUpperCase();
        if (!"BUY".equals(normalized) && !"SELL".equals(normalized)) {
            throw new ApplicationException(ErrorCode.INVALID_ORDER_TYPE);
        }
        return normalized;
    }

    private void validateInput(OrderCreateCommand command) {
        if (command.getAmount() == null || command.getAmount() <= 0) {
            throw new ApplicationException(ErrorCode.AMOUNT_OUT_OF_LIMIT);
        }
        if (command.getPrice() == null || command.getPrice() <= 0) {
            throw new ApplicationException(ErrorCode.AMOUNT_OUT_OF_LIMIT);
        }
        if (command.getMinLimit() != null && command.getMinLimit() <= 0) {
            throw new ApplicationException(ErrorCode.AMOUNT_OUT_OF_LIMIT);
        }
        if (command.getMaxLimit() != null && command.getMaxLimit() <= 0) {
            throw new ApplicationException(ErrorCode.AMOUNT_OUT_OF_LIMIT);
        }
        if (command.getMinLimit() != null && command.getMaxLimit() != null
                && command.getMinLimit() > command.getMaxLimit()) {
            throw new ApplicationException(ErrorCode.AMOUNT_OUT_OF_LIMIT);
        }
    }

    private FiatAccount resolveFiatAccount(User user, OrderCreateCommand command) {
        var accountId = command.getFiatAccountId();
        if (accountId == null) {
            return null;
        }

        FiatAccount account = fiatAccountRepository.findById(accountId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.FIAT_ACCOUNT_NOT_FOUND));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        return account;
    }
    private Order buildOrder(OrderCreateCommand command, User user, String orderType, FiatAccount fiatAccount) {
        Order order = new Order();
        order.setUser(user);
        order.setType(orderType);
        order.setToken(command.getToken());
        order.setAmount(command.getAmount());
        order.setAvailableAmount(command.getAmount());
        order.setPrice(command.getPrice());
        order.setFiat(command.getFiat());
        order.setMinLimit(command.getMinLimit());
        order.setMaxLimit(command.getMaxLimit());
        if (fiatAccount != null) {
            order.setPaymentMethod(fiatAccount.getPaymentType());
        } else {
            order.setPaymentMethod(command.getPaymentMethod());
        }
        if (command.getPriceMode() != null) {
            order.setPriceMode(command.getPriceMode());
        }
        order.setFiatAccount(fiatAccount);
        order.setStatus(OrderStatus.OPEN.name());
        order.setExpireAt(LocalDateTime.now().plusMinutes(ORDER_EXPIRATION_MINUTES));
        return order;
    }

    private OrderResult enrichWithFiatAccount(OrderResult dto, FiatAccount fiatAccount) {
        if (fiatAccount != null) {
            dto.setFiatAccountId(fiatAccount.getId());
            dto.setBankName(fiatAccount.getBankName());
            dto.setBankAccount(fiatAccount.getAccountNumber());
            dto.setAccountHolder(fiatAccount.getAccountHolder());
        }
        return dto;
    }

    private boolean isSellOrder(String orderType) {
        return "SELL".equalsIgnoreCase(orderType);
    }
}
