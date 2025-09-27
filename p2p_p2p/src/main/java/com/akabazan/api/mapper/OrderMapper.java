package com.akabazan.api.mapper;

import com.akabazan.api.request.OrderRequest;
import com.akabazan.repository.entity.Order;
import com.akabazan.service.dto.OrderDTO;
public class OrderMapper {
    public static OrderDTO toDTO(OrderRequest request) {
        OrderDTO dto = new OrderDTO();
        dto.setType(request.getType());
        dto.setToken(request.getToken());
        dto.setAmount(request.getAmount());
        dto.setPrice(request.getPrice());
        dto.setPaymentMethod(request.getPaymentMethod());
        dto.setFiatAccount(request.getFiatAccount());
        dto.setMinLimit(request.getMinLimit());
        dto.setMaxLimit(request.getMaxLimit());
        return dto;
    }
}
