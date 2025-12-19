package com.akabazan.service.partner.dto;

public record LockFundsRequest(
        String requestId,
        String externalUserId,
        String asset,
        String amount,
        String refType,
        String refId
) {}

