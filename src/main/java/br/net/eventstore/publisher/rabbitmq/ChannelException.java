package br.net.eventstore.publisher.rabbitmq;

public class ChannelException extends RuntimeException {

    public ChannelException(String message, Throwable cause) {
        super(message, cause);
    }
}
