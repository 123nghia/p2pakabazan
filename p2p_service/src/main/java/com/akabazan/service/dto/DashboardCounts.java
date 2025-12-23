package com.akabazan.service.dto;

public class DashboardCounts {
    private long activeTrades;
    private long incomingChats;

    public DashboardCounts() {}

    public DashboardCounts(long activeTrades, long incomingChats) {
        this.activeTrades = activeTrades;
        this.incomingChats = incomingChats;
    }

    public long getActiveTrades() {
        return activeTrades;
    }

    public void setActiveTrades(long activeTrades) {
        this.activeTrades = activeTrades;
    }

    public long getIncomingChats() {
        return incomingChats;
    }

    public void setIncomingChats(long incomingChats) {
        this.incomingChats = incomingChats;
    }
}
