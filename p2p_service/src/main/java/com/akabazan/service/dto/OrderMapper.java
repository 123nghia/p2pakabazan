package com.akabazan.service.dto;

import com.akabazan.repository.entity.Order;

public class OrderMapper {

    public static OrderDTO toDTO(Order order) {
        if (order == null) return null;

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        dto.setType(order.getType());
        dto.setToken(order.getToken());
        dto.setAmount(order.getAmount());
        dto.setPrice(order.getPrice());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setMinLimit(order.getMinLimit());
        dto.setMaxLimit(order.getMaxLimit());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
       
        return dto;
    }
}
