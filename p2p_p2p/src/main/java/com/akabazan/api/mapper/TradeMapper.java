package com.akabazan.api.mapper;

import com.akabazan.api.request.OrderRequest;
import com.akabazan.api.request.TradeRequest;
import com.akabazan.repository.entity.Order;

import com.akabazan.service.dto.OrderDTO;
import com.akabazan.service.dto.TradeDTO;
public class TradeMapper {

    // Convert từ API request → DTO để service xử lý
    public static TradeDTO toDTO(TradeRequest request) {
        TradeDTO tradeDTO = new TradeDTO();
        tradeDTO.setOrderId(request.getOrderId());
        tradeDTO.setAmount(request.getAmount());
    
        return tradeDTO;
    }
}