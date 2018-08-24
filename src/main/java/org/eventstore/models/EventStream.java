package org.eventstore.models;

import java.util.List;

public class EventStream  {

    private String streamId;
    private String aggregate;

    public EventStream(String streamId, String aggregate){
        this.streamId = streamId;
        this.aggregate = aggregate;
    }

    public List<Event> getEvents(String streamId){
        return null;
    }

    public List<Event> getEvents(String streamId, String aggregate){
        return null;
    }

    public void addEvent(Event event){

    }

    public String getAggregate() {
        return aggregate;
    }

    public String getStreamId() {
        return streamId;
    }
}
