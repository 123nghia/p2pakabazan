package com.akabazan.api.request;

import java.util.UUID;

public class DisputeAssignRequest {

    private UUID adminId;

    public UUID getAdminId() {
        return adminId;
    }

    public void setAdminId(UUID adminId) {
        this.adminId = adminId;
    }
}
