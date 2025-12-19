package com.akabazan.partner.internal.funds;

public record LockFundsRequest(
        String requestId,
        String externalUserId,
        String asset,
        String amount,
        String refType,
        String refId
) {}

