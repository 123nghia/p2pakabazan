package com.akabazan.api.mapper;

import com.akabazan.api.request.OrderRequest;
import com.akabazan.service.command.OrderCreateCommand;

public final class OrderCommandMapper {

    private OrderCommandMapper() {
    }

    public static OrderCreateCommand toCommand(OrderRequest request) {
        OrderCreateCommand command = new OrderCreateCommand();
        command.setType(request.getType());
        command.setToken(request.getToken());
        command.setAmount(request.getAmount());
        command.setPrice(request.getPrice());
        command.setFiat(request.getFiat());
        command.setPaymentMethod(request.getPaymentMethod());
        command.setMinLimit(request.getMinLimit());
        command.setMaxLimit(request.getMaxLimit());
        command.setFiatAccountId(request.getFiatAccountId());
        command.setPriceMode(request.getPriceMode());

        return command;
    }
}
