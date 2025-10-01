package com.akabazan.service.order.usecase;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.entity.Order;
import com.akabazan.service.order.support.SellerFundsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CloseOrderService implements CloseOrderUseCase {

    private final OrderRepository orderRepository;
    private final SellerFundsManager sellerFundsManager;

    public CloseOrderService(OrderRepository orderRepository, SellerFundsManager sellerFundsManager) {
        this.orderRepository = orderRepository;
        this.sellerFundsManager = sellerFundsManager;
    }

    @Override
    @Transactional
    public void close(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.ORDER_NOT_FOUND));

        if (isSellOrder(order) && order.getAvailableAmount() > 0) {
            // Trả lại coin chưa khớp cho seller
            sellerFundsManager.release(order.getUser().getId(), order.getToken(), order.getAvailableAmount());
            order.setAvailableAmount(0.0); // reset để nhất quán
        }

        order.setStatus(OrderStatus.CLOSED.name());
        orderRepository.save(order);
    }

    private boolean isSellOrder(Order order) {
        return "SELL".equalsIgnoreCase(order.getType());
    }
}
