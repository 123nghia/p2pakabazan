package com.akabazan.service.partner.dto;

public record LockFundsResponse(
        String requestId,
        String lockId,
        String status,
        String lockedAmount,
        String availableAfter,
        String lockedAfter
) {}

