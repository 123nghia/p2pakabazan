package com.akabazan.partner.internal.funds;

public record TransferFundsResponse(
        String requestId,
        String transferId,
        String status,
        String lockRemaining,
        String toAvailableAfter
) {}

