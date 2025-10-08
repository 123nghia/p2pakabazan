package com.akabazan.service.dto;

import com.akabazan.repository.entity.Trade;

public class TradeMapper {

    private TradeMapper() {
    }

    public static TradeResult toResult(Trade trade) {
        TradeResult result = new TradeResult();
        result.setId(trade.getId());
        var userBuyer = trade.getBuyer();
        var userSender = trade.getSeller();
        var order = trade.getOrder();
        result.setOrderId(trade.getOrder().getId());
        result.setBuyerId(trade.getBuyer().getId());
        result.setSellerId(trade.getSeller().getId());
        result.setAmount(trade.getAmount());
        result.setStatus(trade.getStatus().name());
        result.setEscrow(trade.isEscrow());
        result.setTradeCode(trade.getTradeCode());
        result.setBuyerUserName(userBuyer.getEmail());
        result.setSenderUserName(userSender.getEmail());
        result.setCreatedAt(trade.getCreatedAt());
        result.setPrice(trade.getOrder().getPrice());
        result.setToken(order.getToken());
        result.setFiat(order.getFiat());
        if (trade.getSellerFiatAccount() != null) {
            result.setSellerFiatAccountId(trade.getSellerFiatAccount().getId());
        }
        result.setSellerBankName(trade.getSellerBankName());
        result.setSellerAccountNumber(trade.getSellerAccountNumber());
        result.setSellerAccountHolder(trade.getSellerAccountHolder());
        result.setSellerBankBranch(trade.getSellerBankBranch());
        result.setSellerPaymentType(trade.getSellerPaymentType());
        
        
        return result;
    }
}
