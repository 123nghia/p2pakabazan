package com.akabazan.partner.internal.funds;

public record UnlockFundsResponse(
        String requestId,
        String lockId,
        String status,
        String unlockedAmount,
        String lockRemaining,
        String availableAfter,
        String lockedAfter
) {}

