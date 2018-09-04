package br.net.eventstore.publisher;

public class SubscriptionException extends RuntimeException {

    public SubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
