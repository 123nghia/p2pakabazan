package com.akabazan.service;

public interface MarketService {
    Double getP2PPrice(String token, String fiat, String tradeType, int top) throws Exception;
}