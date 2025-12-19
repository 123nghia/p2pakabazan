package com.akabazan.service.partner.dto;

public record UnlockFundsRequest(
        String requestId,
        String lockId,
        String amount
) {}

