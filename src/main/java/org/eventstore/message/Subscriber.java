package org.eventstore.message;

import org.eventstore.models.Message;

public interface Subscriber {
    void on(Message message);
}
