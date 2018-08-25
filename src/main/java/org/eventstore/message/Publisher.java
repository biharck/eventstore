package org.eventstore.message;

import org.eventstore.models.Message;

public interface Publisher {
    void publish(Message message);
}
