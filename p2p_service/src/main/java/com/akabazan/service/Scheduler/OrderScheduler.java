package com.akabazan.service.Scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.akabazan.service.OrderService;

@Component
public class OrderScheduler {

    private final OrderService orderService;

    public OrderScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    /** Chạy mỗi phút để expire order */
    @Scheduled(fixedDelay = 60000)
    public void autoExpireOrders() {
        // orderService.expireOrders();
    }
}