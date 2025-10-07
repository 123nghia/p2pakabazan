package com.akabazan.api.mapper;

import com.akabazan.api.reponse.TradeInfoResponse;
import com.akabazan.service.dto.TradeInfoResult;

public final class TradeInfoResponseMapper {
    private TradeInfoResponseMapper() {}

    public static TradeInfoResponse from(TradeInfoResult r) {
        TradeInfoResponse resp = new TradeInfoResponse();
        resp.setTradeId(r.getTradeId());
        resp.setTradeCode(r.getTradeCode());
        resp.setOrderType(r.getOrderType());
        resp.setStatus(r.getStatus());
        resp.setAmount(r.getAmount());

        resp.setBankName(r.getBankName());
        resp.setAccountNumber(r.getAccountNumber());
        resp.setAccountHolder(r.getAccountHolder());
        resp.setRole(r.getRole());
        resp.setPrice(r.getPrice());
        resp.setAutoCancelAt(r.getAutoCancelAt());
        resp.setTimeRemainingSeconds(r.getTimeRemainingSeconds());
        resp.setCanCancel(r.isCanCancel());
        
        return resp;
    }
}