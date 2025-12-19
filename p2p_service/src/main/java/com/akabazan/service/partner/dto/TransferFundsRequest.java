package com.akabazan.service.partner.dto;

public record TransferFundsRequest(
        String requestId,
        String lockId,
        String fromExternalUserId,
        String toExternalUserId,
        String asset,
        String amount,
        String refType,
        String refId
) {}

