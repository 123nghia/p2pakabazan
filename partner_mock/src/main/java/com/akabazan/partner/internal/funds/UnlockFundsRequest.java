package com.akabazan.partner.internal.funds;

public record UnlockFundsRequest(
        String requestId,
        String lockId,
        String amount
) {}

