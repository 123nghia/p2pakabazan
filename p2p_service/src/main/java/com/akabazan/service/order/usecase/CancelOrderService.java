package com.akabazan.service.order.usecase;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.User;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.order.support.SellerFundsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CancelOrderService implements CancelOrderUseCase {

    private final CurrentUserService currentUserService;
    private final OrderRepository orderRepository;
    private final SellerFundsManager sellerFundsManager;
    private final TradeRepository tradeRepository;

    public CancelOrderService(CurrentUserService currentUserService,
                              OrderRepository orderRepository,
                              SellerFundsManager sellerFundsManager,
                              TradeRepository tradeRepository) {
        this.currentUserService = currentUserService;
        this.orderRepository = orderRepository;
        this.sellerFundsManager = sellerFundsManager;
        this.tradeRepository = tradeRepository;
    }

    @Override
    public void cancel(UUID orderId) {
        User seller = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(seller.getId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }

        if (!OrderStatus.OPEN.name().equals(order.getStatus())) {
            throw new ApplicationException(ErrorCode.ORDER_CLOSED);
        }

        long activeTrades = tradeRepository.countByOrderIdAndStatusNotIn(
                orderId,
                List.of(TradeStatus.CANCELLED, TradeStatus.COMPLETED));

        if (activeTrades > 0) {
            throw new ApplicationException(ErrorCode.ORDER_HAS_TRADE);
        }

        if (isSellOrder(order) && order.getAvailableAmount() > 0) {
            sellerFundsManager.unlockSellOrderRemainder(order, order.getAvailableAmount());
            order.setAvailableAmount(0.0);
        }

        order.setStatus(OrderStatus.CANCELLED.name());
        orderRepository.save(order);
    }

    private boolean isSellOrder(Order order) {
        return "SELL".equalsIgnoreCase(order.getType());
    }
}
