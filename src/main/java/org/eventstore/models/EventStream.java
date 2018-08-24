package org.eventstore.models;

import java.util.List;

public class EventStream  {

    private String streamId;
    private String aggregate;

    public EventStream(String streamId, String aggregate){
        this.streamId = streamId;
    }

    public List<Event> getEvents(){
        return null;
    }


}
