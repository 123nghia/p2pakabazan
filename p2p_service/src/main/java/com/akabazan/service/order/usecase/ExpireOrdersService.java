package com.akabazan.service.order.usecase;

import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.entity.Order;
import com.akabazan.service.order.support.SellerFundsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ExpireOrdersService implements ExpireOrdersUseCase {

    private final OrderRepository orderRepository;
    private final SellerFundsManager sellerFundsManager;

    public ExpireOrdersService(OrderRepository orderRepository,
            SellerFundsManager sellerFundsManager) {
        this.orderRepository = orderRepository;
        this.sellerFundsManager = sellerFundsManager;
    }

    @Override
    @Transactional
    public void expire() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> expiredOrders = orderRepository.findAllByStatusAndExpireAtBefore(OrderStatus.OPEN.name(), now);

        for (Order order : expiredOrders) {
            if (isSellOrder(order) && order.getAvailableAmount() > 0) {
                // Trả lại coin cho seller
                sellerFundsManager.unlockSellOrderRemainder(order, order.getAvailableAmount());
                order.setAvailableAmount(0.0); // reset vì order đã hết hạn
            }

            order.setStatus(OrderStatus.EXPIRED.name());
            orderRepository.save(order);
        }
    }

    private boolean isSellOrder(Order order) {
        return "SELL".equalsIgnoreCase(order.getType());
    }
}
