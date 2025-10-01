package com.akabazan.api.mapper;

import com.akabazan.api.request.TradeRequest;
import com.akabazan.service.command.TradeCreateCommand;

public final class TradeCommandMapper {

    private TradeCommandMapper() {
    }

    // Convert từ API request → Command để service xử lý
    public static TradeCreateCommand toCommand(TradeRequest request) {
        TradeCreateCommand command = new TradeCreateCommand();
        command.setOrderId(request.getOrderId());
        if (request.getAmount() != null) {
            command.setAmount(request.getAmount());
        }
        command.setChatMessage(request.getChatMessage());

        return command;
    }
}
