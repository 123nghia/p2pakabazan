package com.akabazan.service.partner.dto;

public record TransferFundsResponse(
        String requestId,
        String transferId,
        String status,
        String lockRemaining,
        String toAvailableAfter
) {}

