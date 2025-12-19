package com.akabazan.service.partner.dto;

import java.util.List;

public record BalancesResponse(String externalUserId, List<AssetBalance> balances) {
    public record AssetBalance(String asset, String available, String locked) {}
}

