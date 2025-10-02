package com.akabazan.api.request;

public class DisputeResolutionRequest {

    private String outcome;
    private String note;

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
