package com.akabazan.common.constant;

public enum ErrorCode {
    UNAUTHORIZED("USER_002", "User unauthorized"),
    USER_NOT_FOUND("USER_001", "User not found"),
    INVALID_CREDENTIALS("AUTH_001", "Invalid credentials"),
    USER_ALREADY_EXISTS("USER_003", "User already exists"),
    KYC_REQUIRED("USER_002", "User must be KYC verified"),
    INSUFFICIENT_BALANCE("WALLET_001", "Insufficient balance"),
    ORDER_NOT_FOUND("ORDER_001", "Order not found"),
    ORDER_CLOSED("ORDER_002", "Order is closed"),
    ORDER_HAS_TRADE("ORDER_004", "Order has existing trades"),
    TRADE_NOT_FOUND("TRADE_001", "Trade not found"),
    INVALID_TRADE_STATUS("TRADE_002", "Invalid trade status"),
    AMOUNT_OUT_OF_LIMIT("ORDER_003", "Amount out of limits"),
    ALREADY_IN_DISPUTE("TRADE_003", "Trade already in dispute"),
    DISPUTE_NOT_FOUND("DISPUTE_001", "Dispute not found"),
    INVALID_DISPUTE_STATUS("DISPUTE_002", "Invalid dispute status"),
    INVALID_TOKEN("Token_001", "Invalid or expired token"),
    WALLET_NOT_FOUND("WALLET_001", "wallet is not found"),
    INVALID_ORDER_TYPE("ORDER_TYPE_001", "order type is not found"),
    FORBIDDEN("AUTH_002", "Access denied"),
    NOTIFICATION_NOT_FOUND("NOTIFICATION_001", "Notification not found"),
    INVALID_CURRENCY_TYPE("CURRENCY_001", "Currency type is invalid"),
    FIAT_ACCOUNT_NOT_FOUND("FIAT_001", "Seller payment account not found"),
    SELLER_PAYMENT_METHOD_REQUIRED("FIAT_002", "Seller payment method is required"),
    FIAT_ACCOUNT_ALREADY_EXISTS("FIAT_003", "Fiat account already exists"),
    INVALID_FIAT_ACCOUNT_INPUT("FIAT_004", "Fiat account information is invalid"),
    INVALID_PAYMENT_METHOD_TYPE("PAYMENT_METHOD_001", "Payment method type is invalid");
   
   

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
