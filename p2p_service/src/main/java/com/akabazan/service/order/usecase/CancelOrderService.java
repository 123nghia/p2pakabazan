package com.akabazan.service.order.usecase;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.User;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.order.support.SellerFundsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CancelOrderService implements CancelOrderUseCase {

    private final CurrentUserService currentUserService;
    private final OrderRepository orderRepository;
    private final SellerFundsManager sellerFundsManager;

    public CancelOrderService(CurrentUserService currentUserService,
                              OrderRepository orderRepository,
                              SellerFundsManager sellerFundsManager) {
        this.currentUserService = currentUserService;
        this.orderRepository = orderRepository;
        this.sellerFundsManager = sellerFundsManager;
    }

    @Override
    public void cancel(Long orderId) {
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

        if (isSellOrder(order) && order.getAvailableAmount() > 0) {
            sellerFundsManager.release(order.getUser().getId(), order.getToken(), order.getAvailableAmount());
        }

        order.setStatus(OrderStatus.CANCELLED.name());
        orderRepository.save(order);
    }

    private boolean isSellOrder(Order order) {
        return "SELL".equalsIgnoreCase(order.getType());
    }
}
