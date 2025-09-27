package com.akabazan.service.dto;

import com.akabazan.repository.entity.Order;
public class OrderMapper {

    public static OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setType(order.getType());
        dto.setToken(order.getToken());
        dto.setAmount(order.getAmount());
        dto.setPrice(order.getPrice());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setFiatAccount(order.getFiatAccount()); // map fiatAccount
        dto.setMinLimit(order.getMinLimit());
        dto.setMaxLimit(order.getMaxLimit());
        dto.setStatus(order.getStatus());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        return dto;
    }

    public static Order toEntity(OrderDTO dto, Order order) {
        if (order == null) {
            order = new Order();
        }
        order.setType(dto.getType());
        order.setToken(dto.getToken());
        order.setAmount(dto.getAmount());
        order.setPrice(dto.getPrice());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setFiatAccount(dto.getFiatAccount());
        order.setMinLimit(dto.getMinLimit());
        order.setMaxLimit(dto.getMaxLimit());
        order.setStatus(dto.getStatus());
        return order;
    }




}
