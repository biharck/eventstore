package org.eventstore.models;

import java.sql.Timestamp;

public class Event {

    private String payload;
    private Timestamp commitTimestamp;
    private long sequence;

}
