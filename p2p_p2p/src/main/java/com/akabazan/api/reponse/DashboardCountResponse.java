package com.akabazan.api.reponse;

public class DashboardCountResponse {
    private long activeTrades;
    private long incomingChats;

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
