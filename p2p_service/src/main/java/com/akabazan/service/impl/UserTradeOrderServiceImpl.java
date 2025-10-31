package com.akabazan.service.impl;

import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.Trade;
import com.akabazan.service.UserTradeOrderService;
import com.akabazan.service.dto.OrderMapper;
import com.akabazan.service.dto.UserTradesOrdersResult;
import com.akabazan.service.dto.TradeMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserTradeOrderServiceImpl implements UserTradeOrderService {

    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;

    public UserTradeOrderServiceImpl(TradeRepository tradeRepository,
                                     OrderRepository orderRepository) {
        this.tradeRepository = tradeRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserTradesOrdersResult getUserTradesAndOrders(UUID userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<Trade> trades = tradeRepository.findByUser(userId);

        return new UserTradesOrdersResult(
                orders.stream().map(OrderMapper::toResult).toList(),
                trades.stream().map(TradeMapper::toResult).toList()
        );
    }
}
