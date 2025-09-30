package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.FiatAccountRepository;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.UserRepository;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.entity.FiatAccount;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.service.OrderService;
import com.akabazan.service.UserService;
import com.akabazan.service.UserTradeOrderService;
import com.akabazan.service.dto.OrderDTO;
import com.akabazan.service.dto.OrderMapper;
import com.akabazan.service.dto.TradeMapper;
import com.akabazan.service.dto.UserTradesOrdersDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class UserTradeOrderServiceImpl implements UserTradeOrderService {
  private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;


    public UserTradeOrderServiceImpl(TradeRepository tradeRepository,                         
                            OrderRepository orderRepository 
                            ) {
        this.tradeRepository = tradeRepository;
        this.orderRepository = orderRepository;
      
    }
    @Override
    @Transactional(readOnly = true)
    public UserTradesOrdersDTO getUserTradesAndOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<Trade> trades = tradeRepository.findByUser(userId);

        return new UserTradesOrdersDTO(
                orders.stream().map(OrderMapper::toDto).toList(),
                trades.stream().map(TradeMapper::toDTO).toList()
        );
    }
}
