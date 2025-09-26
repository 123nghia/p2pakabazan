package com.akabazan.common.constant;

public enum ErrorCode {
    USER_NOT_FOUND("USER_001", "User not found"),
    INVALID_CREDENTIALS("AUTH_001", "Invalid credentials"),
    KYC_REQUIRED("USER_002", "User must be KYC verified"),
    INSUFFICIENT_BALANCE("WALLET_001", "Insufficient balance"),
    ORDER_NOT_FOUND("ORDER_001", "Order not found"),
    ORDER_CLOSED("ORDER_002", "Order is closed"),
    TRADE_NOT_FOUND("TRADE_001", "Trade not found"),
    INVALID_TRADE_STATUS("TRADE_002", "Invalid trade status"),
    AMOUNT_OUT_OF_LIMIT("ORDER_003", "Amount out of limits"),
    ALREADY_IN_DISPUTE("TRADE_003", "Trade already in dispute"),
    INVALID_TOKEN("Token_001", "Invalid or expired token");
   
   

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}