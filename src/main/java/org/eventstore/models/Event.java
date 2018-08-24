package org.eventstore.models;

public class Event {

    private String payload;
    private long commitTimestamp;
    private long sequence;

    public Event(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence){
        this.sequence = sequence;
    }

    public long getCommitTimestamp() {
        return commitTimestamp;
    }

    public void setCommitTimestamp(long commitTimestamp) {
        this.commitTimestamp = commitTimestamp;
    }
}
