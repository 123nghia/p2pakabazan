package com.akabazan.api.mapper;

import com.akabazan.api.request.OrderRequest;
import com.akabazan.repository.entity.Order;
import com.akabazan.service.dto.OrderResult;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResult toResult(OrderRequest request) {
        OrderResult result = new OrderResult();
        result.setType(request.getType());
        result.setToken(request.getToken());
        result.setAmount(request.getAmount());
        result.setPrice(request.getPrice());
        result.setPaymentMethod(request.getPaymentMethod());
        result.setMinLimit(request.getMinLimit());
        result.setMaxLimit(request.getMaxLimit());
        result.setBankName(request.getBankName());
        result.setBankAccount(request.getBankAccount());
        result.setAccountHolder(request.getAccountHolder());
        result.setPriceMode(request.getPriceMode());

        return result;
    }
}
