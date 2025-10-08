package com.akabazan.service.dto;

import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.Trade;

import java.util.List;
import java.util.stream.Collectors;

import com.akabazan.repository.entity.FiatAccount;
import com.akabazan.repository.entity.User;

public class OrderMapper {

    public static OrderResult toResult(Order order) {
        if (order == null) return null;

        OrderResult dto = new OrderResult();
        dto.setId(order.getId());
        dto.setType(order.getType());
        dto.setToken(order.getToken());
        dto.setAmount(order.getAmount());
        dto.setPrice(order.getPrice());
        dto.setFiat(order.getFiat());
        dto.setMinLimit(order.getMinLimit());
        dto.setMaxLimit(order.getMaxLimit());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPriceMode(order.getPriceMode());
        dto.setAvailableAmount(order.getAvailableAmount());
        dto.setExpireAt(order.getExpireAt());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getFiatAccount() != null) {
            dto.setFiatAccountId(order.getFiatAccount().getId());

            dto.setBankName(order.getFiatAccount().getBankName());
            dto.setBankAccount(order.getFiatAccount().getAccountNumber());
            dto.setAccountHolder(order.getFiatAccount().getAccountHolder());
            dto.setPaymentType(order.getFiatAccount().getPaymentType());
            dto.setBankBranch(order.getFiatAccount().getBranch());
            
        }
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUserName(extractDisplayName(order.getUser().getEmail()));
        }


        return dto;
    }

     public static OrderResult toResult(Order order,  List<Trade> trades) {
        if (order == null) return null;

        OrderResult dto = new OrderResult();
        dto.setId(order.getId());
        dto.setType(order.getType());
        dto.setToken(order.getToken());
        dto.setAmount(order.getAmount());
        dto.setPrice(order.getPrice());
        dto.setFiat(order.getFiat());
        dto.setMinLimit(order.getMinLimit());
        dto.setMaxLimit(order.getMaxLimit());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPriceMode(order.getPriceMode());
        dto.setAvailableAmount(order.getAvailableAmount());
        dto.setExpireAt(order.getExpireAt());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getFiatAccount() != null) {
            dto.setFiatAccountId(order.getFiatAccount().getId());

            dto.setBankName(order.getFiatAccount().getBankName());
            dto.setBankAccount(order.getFiatAccount().getAccountNumber());
            dto.setAccountHolder(order.getFiatAccount().getAccountHolder());
            dto.setPaymentType(order.getFiatAccount().getPaymentType());
            dto.setBankBranch(order.getFiatAccount().getBranch());
            
        }
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUserName(extractDisplayName(order.getUser().getEmail()));
        }

        List<TradeResult> tradeResults = trades.stream()
        .map(TradeMapper::toResult)
        .collect(Collectors.toList());
        dto.setTrades(tradeResults);


        return dto;
    }

    private static String extractDisplayName(String email) {
        if (email == null) {
            return null;
        }
        int atIndex = email.indexOf('@');
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }

    public static Order toEntity(OrderResult dto, FiatAccount fiatAccount, User user) {
        if (dto == null) return null;

        Order order = new Order();
        order.setId(dto.getId());
        order.setType(dto.getType());
        order.setToken(dto.getToken());
        order.setAmount(dto.getAmount());
        order.setPrice(dto.getPrice());
        order.setFiat(dto.getFiat());
        order.setMinLimit(dto.getMinLimit());
        order.setMaxLimit(dto.getMaxLimit());
        order.setStatus(dto.getStatus());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setPriceMode(dto.getPriceMode());
        order.setAvailableAmount(dto.getAvailableAmount());
        order.setExpireAt(dto.getExpireAt());

        order.setFiatAccount(fiatAccount);
        order.setUser(user);

        return order;
    }
}
