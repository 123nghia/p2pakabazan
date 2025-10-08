package com.akabazan.api.mapper;

import com.akabazan.api.reponse.TradeResponse;
import com.akabazan.service.dto.TradeResult;

public final class TradeResponseMapper {

    private TradeResponseMapper() {
    }

    public static TradeResponse from(TradeResult result) {
        if (result == null) {
            return null;
        }
        TradeResponse response = new TradeResponse();
        response.setId(result.getId());
        response.setOrderId(result.getOrderId());
        response.setBuyerId(result.getBuyerId());
        response.setSellerId(result.getSellerId());
        response.setAmount(result.getAmount());
        response.setStatus(result.getStatus());
     
        response.setTradeCode(result.getTradeCode());
        response.setPrice(result.getPrice());
        response.setCreatedAt(result.getCreatedAt());
        response.setBuyerUserName(result.getBuyerUserName());
        response.setSenderUserName(result.getSenderUserName());
        response.setToken(result.getToken());
        response.setFiat(result.getFiat());
        response.setRole(result.getRole());
        response.setCanCancel(result.isCanCancel());
        response.setCounterparty(result.getCounterparty());
        response.setSellerFiatAccountId(result.getSellerFiatAccountId());
        response.setSellerBankName(result.getSellerBankName());
        response.setSellerAccountNumber(result.getSellerAccountNumber());
        response.setSellerAccountHolder(result.getSellerAccountHolder());
        response.setSellerBankBranch(result.getSellerBankBranch());
        response.setSellerPaymentType(result.getSellerPaymentType());
        return response;
    }
}
